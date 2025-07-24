// src/main/java/com/microcredito/controller/EmprestimoController.java
package com.microcredito.controller;

import com.microcredito.entity.Emprestimo;
import com.microcredito.entity.Parcela;
import com.microcredito.enums.StatusEmprestimo;
import com.microcredito.service.CalculoEmprestimoService;
import com.microcredito.service.ClienteService;
import com.microcredito.service.EmprestimoService;
import com.microcredito.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/emprestimos")
public class EmprestimoController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private EmprestimoService service;

    @Autowired
    private CalculoEmprestimoService calculoEmprestimoService;

    @Autowired
    private PdfService pdfService;

    @GetMapping
    public String listar(@RequestParam(required = false) StatusEmprestimo status,
                         @RequestParam(required = false) Long clienteId,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size,
                         Model model) {

        Page<Emprestimo> emprestimos = service.listarEmprestimos(status, clienteId, page, size);
        model.addAttribute("emprestimos", emprestimos);
        model.addAttribute("clientes", clienteService.findAll());
        model.addAttribute("status", status);
        model.addAttribute("clienteId", clienteId);
        return "emprestimos";
    }

    @GetMapping("/{id}/pdf")
    @ResponseBody
    public ResponseEntity<byte[]> gerarPlanoPDF(@PathVariable Long id) {
        byte[] pdfBytes = pdfService.gerarPlanoPagamentoPdf(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("plano_pagamento.pdf").build());
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("emprestimo") Emprestimo emprestimo) {
        service.save(emprestimo);
        return "redirect:/emprestimos";
    }

    @GetMapping("/detalhes/{id}")
    public String detalhes(@PathVariable Long id, Model model) {
        Optional<Emprestimo> emprestimo = service.findById(id);
        if (emprestimo.isPresent()) {
            model.addAttribute("emprestimo", emprestimo.get());
            return "fragments/detalhes-emprestimo :: detalhesContent";
        } else {
            return "redirect:/emprestimos";
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.deleteById(id);
        return "redirect:/emprestimos";
    }

    @PostMapping("/calcular-amortizacao")
    @ResponseBody
    public ResponseEntity<List<ParcelaDTO>> calcularAmortizacao(@RequestBody Map<String, String> payload) {
        try {
            BigDecimal valorEmprestimo = new BigDecimal(payload.get("valorEmprestimo"));
            int numParcelas = Integer.parseInt(payload.get("numParcelas"));
            double taxaJuroAnual = Double.parseDouble(payload.get("taxaJuro"));
            String tipoAmortizacao = payload.get("tipoAmortizacao");
            String frequencia = payload.get("frequencia");
            LocalDate dataContratacao = LocalDate.parse(payload.get("dataContratacao"));

            List<Parcela> parcelas;
            BigDecimal taxaPeriodo = calculoEmprestimoService.calcularTaxaPeriodo(BigDecimal.valueOf(taxaJuroAnual), frequencia);

            if ("CAPITAL_CONSTANTE".equalsIgnoreCase(tipoAmortizacao)) {
                parcelas = calculoEmprestimoService.calcularCapitalConstante(valorEmprestimo, numParcelas, taxaPeriodo, dataContratacao, frequencia);
            } else {
                parcelas = calculoEmprestimoService.calcularPrestacoesConstantes(valorEmprestimo, numParcelas, taxaPeriodo, dataContratacao, frequencia);
            }

            List<ParcelaDTO> dtoList = new ArrayList<>();
            BigDecimal saldoDevedor = valorEmprestimo;
            for (Parcela p : parcelas) {
                dtoList.add(new ParcelaDTO(
                        p.getNumeroParcela(),
                        p.getDataVencimento().toString(),
                        p.getValorAmortizacao(),
                        p.getValorJuros(),
                        p.getValorPrestacao(),
                        saldoDevedor
                ));
                saldoDevedor = saldoDevedor.subtract(p.getValorAmortizacao());
            }

            return ResponseEntity.ok(dtoList);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DTO simples para enviar dados do plano
    public record ParcelaDTO(
            Integer numero,
            String dataVencimento,
            BigDecimal valorAmortizacao,
            BigDecimal valorJuros,
            BigDecimal valorPrestacao,
            BigDecimal saldoDevedor
    ) {}
}