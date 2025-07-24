// src/main/java/com/microcredito/service/CalculoEmprestimoService.java
package com.microcredito.service;

import com.microcredito.entity.Parcela;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalculoEmprestimoService {

    public List<Parcela> calcularCapitalConstante(BigDecimal valorTotal, int numeroParcelas, BigDecimal taxaPeriodo, LocalDate dataInicio, String frequencia) {
        List<Parcela> parcelas = new ArrayList<>();

        BigDecimal amortizacaoConstante = valorTotal.divide(BigDecimal.valueOf(numeroParcelas), 2, RoundingMode.HALF_UP);
        BigDecimal saldoDevedor = valorTotal;

        for (int i = 1; i <= numeroParcelas; i++) {
            BigDecimal juros = saldoDevedor.multiply(taxaPeriodo).setScale(2, RoundingMode.HALF_UP);
            BigDecimal prestacao = amortizacaoConstante.add(juros);

            LocalDate dataVencimento = calcularDataVencimento(dataInicio, i, frequencia);

            Parcela parcela = new Parcela();
            parcela.setNumeroParcela(i);
            parcela.setValorAmortizacao(amortizacaoConstante);
            parcela.setValorJuros(juros);
            parcela.setValorPrestacao(prestacao);
            parcela.setDataVencimento(dataVencimento);
            parcela.setStatus("PENDENTE");

            parcelas.add(parcela);

            saldoDevedor = saldoDevedor.subtract(amortizacaoConstante);
        }

        return parcelas;
    }

    public List<Parcela> calcularPrestacoesConstantes(BigDecimal valorTotal, int numeroParcelas, BigDecimal taxaPeriodo, LocalDate dataInicio, String frequencia) {
        List<Parcela> parcelas = new ArrayList<>();

        BigDecimal fator = BigDecimal.ONE.add(taxaPeriodo).pow(numeroParcelas);
        BigDecimal prestacao = valorTotal.multiply(taxaPeriodo).multiply(fator).divide(fator.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);

        BigDecimal saldoDevedor = valorTotal;

        for (int i = 1; i <= numeroParcelas; i++) {
            BigDecimal juros = saldoDevedor.multiply(taxaPeriodo).setScale(2, RoundingMode.HALF_UP);
            BigDecimal amortizacao = prestacao.subtract(juros);

            LocalDate dataVencimento = calcularDataVencimento(dataInicio, i, frequencia);

            Parcela parcela = new Parcela();
            parcela.setNumeroParcela(i);
            parcela.setValorAmortizacao(amortizacao);
            parcela.setValorJuros(juros);
            parcela.setValorPrestacao(prestacao);
            parcela.setDataVencimento(dataVencimento);
            parcela.setStatus("PENDENTE");

            parcelas.add(parcela);

            saldoDevedor = saldoDevedor.subtract(amortizacao);
        }

        return parcelas;
    }

    public BigDecimal calcularTaxaPeriodo(BigDecimal taxaMensalPercent, String frequencia) {
        BigDecimal taxaAnual = taxaMensalPercent.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
        int periodsPerYear = switch (frequencia.toUpperCase()) {
            case "DIARIO" -> 30;
            case "SEMANAL" -> 4;
            case "QUINZENAL" -> 2;
            case "MENSAL" -> 1;
            default -> 1;
        };
        return taxaAnual.divide(BigDecimal.valueOf(periodsPerYear), 6, RoundingMode.HALF_UP);
    }

    private LocalDate calcularDataVencimento(LocalDate dataInicio, int parcelaNumero, String frequencia) {
        return switch (frequencia.toUpperCase()) {
            case "DIARIO" -> dataInicio.plusDays(parcelaNumero - 1);
            case "SEMANAL" -> dataInicio.plusWeeks(parcelaNumero - 1);
            case "QUINZENAL" -> dataInicio.plusWeeks((parcelaNumero - 1) * 2);
            case "MENSAL" -> dataInicio.plusMonths(parcelaNumero - 1);
            default -> dataInicio.plusMonths(parcelaNumero - 1);
        };
    }
}