// src/main/java/com/microcredito/service/PdfService.java
package com.microcredito.service;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.microcredito.entity.Emprestimo;
import com.microcredito.entity.Parcela;
import com.microcredito.repository.EmprestimoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;

@Service
public class PdfService {

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    public byte[] gerarPlanoPagamentoPdf(Long id) {
        Optional<Emprestimo> opt = emprestimoRepository.findById(id);
        if (opt.isPresent()) {
            Emprestimo emprestimo = opt.get();
            List<Parcela> parcelas = emprestimo.getParcelas();

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                PdfWriter writer = new PdfWriter(baos);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                document.add(new Paragraph("Plano de Pagamento"));
                document.add(new Paragraph("Cliente: " + emprestimo.getCliente().getNome() + " " + emprestimo.getCliente().getSobrenome()));
                document.add(new Paragraph("Valor do Empréstimo: " + emprestimo.getValorEmprestimo()));
                document.add(new Paragraph("Data de Contratação: " + emprestimo.getDataContratacao()));
                document.add(new Paragraph("Tipo de Amortização: " + emprestimo.getTipoAmortizacao()));
                document.add(new Paragraph("Taxa de Juros Anual: " + emprestimo.getTaxaJurosAnual() + "%"));
                for (Parcela p : parcelas) {
                    document.add(new Paragraph("Parcela " + p.getNumeroParcela()
                            + " - Amortização: " + p.getValorAmortizacao()
                            + ", Juros: " + p.getValorJuros()
                            + ", Prestação: " + p.getValorPrestacao()
                            + ", Vencimento: " + p.getDataVencimento()));
                }

                document.close();
                return baos.toByteArray();
            } catch (Exception e) {
                throw new RuntimeException("Erro ao gerar PDF", e);
            }
        } else {
            throw new IllegalArgumentException("Empréstimo não encontrado");
        }
    }
}