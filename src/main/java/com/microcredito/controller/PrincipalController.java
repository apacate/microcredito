package com.microcredito.controller;

import com.microcredito.service.EmprestimoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/principal")
public class PrincipalController {

    @Autowired
    private EmprestimoService emprestimoService;

    @GetMapping
    public String showPrincipal(Model model) {
        emprestimoService.atualizarStatusParcelas();

        // Dados para os cards
        model.addAttribute("emprestimos", emprestimoService.findAll());
        model.addAttribute("countAtime", emprestimoService.contarParcelasPorStatus("A TEMPO"));
        model.addAttribute("countPendente", emprestimoService.contarParcelasPorStatus("PENDENTE"));
        model.addAttribute("countVencido", emprestimoService.contarParcelasPorStatus("VENCIDO"));
        model.addAttribute("countMes", emprestimoService.contarEmprestimosDoMesAtual());

        // Dados para o gráfico de status de parcelas
        model.addAttribute("statusParcelas", Map.of(
                "Em dia", emprestimoService.contarParcelasPorStatus("A TEMPO"),
                "Pendentes", emprestimoService.contarParcelasPorStatus("PENDENTE"),
                "Vencidas", emprestimoService.contarParcelasPorStatus("VENCIDO")
        ));

        // Dados para o gráfico de empréstimos por mês
        model.addAttribute("emprestimosPorMes", emprestimoService.contarEmprestimosPorMesUltimos12Meses());

        return "principal";
    }
}