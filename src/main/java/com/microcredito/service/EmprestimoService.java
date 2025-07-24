// src/main/java/com/microcredito/service/EmprestimoService.java
package com.microcredito.service;

import com.microcredito.entity.Emprestimo;
import com.microcredito.entity.Parcela;
import com.microcredito.enums.StatusEmprestimo;
import com.microcredito.repository.EmprestimoRepository;
import com.microcredito.repository.ParcelaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
public class EmprestimoService {

    @Autowired
    private EmprestimoRepository repository;

    @Autowired
    private CalculoEmprestimoService calculoEmprestimoService;

    @Autowired
    private ParcelaRepository parcelaRepository;

    public Page<Emprestimo> listarEmprestimos(StatusEmprestimo status, Long clienteId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findFiltrado(status, clienteId, pageable);
    }

    public List<Emprestimo> findAll() {
        return repository.findAll();
    }

    public Optional<Emprestimo> findById(Long id) {
        return repository.findById(id);
    }

    public void save(Emprestimo emprestimo) {
        BigDecimal taxaPeriodo = calculoEmprestimoService.calcularTaxaPeriodo(emprestimo.getTaxaJurosAnual(), emprestimo.getFrequenciaPagamento());

        List<Parcela> parcelas = "CAPITAL_CONSTANTE".equalsIgnoreCase(emprestimo.getTipoAmortizacao()) ?
                calculoEmprestimoService.calcularCapitalConstante(emprestimo.getValorEmprestimo(), emprestimo.getNumeroParcelas(), taxaPeriodo, emprestimo.getDataContratacao(), emprestimo.getFrequenciaPagamento()) :
                calculoEmprestimoService.calcularPrestacoesConstantes(emprestimo.getValorEmprestimo(), emprestimo.getNumeroParcelas(), taxaPeriodo, emprestimo.getDataContratacao(), emprestimo.getFrequenciaPagamento());

        emprestimo.setParcelas(parcelas);
        for (Parcela p : parcelas) {
            p.setEmprestimo(emprestimo);
        }
        emprestimo.setStatus(StatusEmprestimo.PENDENTE);

        repository.save(emprestimo);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public long contarParcelasPorStatus(String status) {
        return parcelaRepository.countByStatus(status);
    }

    public long contarEmprestimosDoMesAtual() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth());
        return repository.countByDataContratacaoBetween(startOfMonth, endOfMonth);
    }

    public void atualizarStatusParcelas() {
        List<Parcela> pendentes = parcelaRepository.findByStatus("PENDENTE");
        Set<Emprestimo> emprestimosToUpdate = new HashSet<>();
        for (Parcela p : pendentes) {
            if (p.getDataVencimento().isBefore(LocalDate.now())) {
                p.setStatus("VENCIDO");
                emprestimosToUpdate.add(p.getEmprestimo());
            }
        }
        parcelaRepository.saveAll(pendentes);

        for (Emprestimo e : emprestimosToUpdate) {
            updateEmprestimoStatus(e);
        }
    }

    private void updateEmprestimoStatus(Emprestimo emprestimo) {
        List<Parcela> parcelas = emprestimo.getParcelas();
        if (parcelas.stream().anyMatch(p -> "VENCIDO".equals(p.getStatus()))) {
            emprestimo.setStatus(StatusEmprestimo.VENCIDO);
        } else if (parcelas.stream().allMatch(p -> "PAGO".equals(p.getStatus()))) {
            emprestimo.setStatus(StatusEmprestimo.A_TEMPO);
        } else {
            emprestimo.setStatus(StatusEmprestimo.PENDENTE);
        }
        repository.save(emprestimo);
    }


    public Map<String, Long> contarEmprestimosPorMesUltimos12Meses() {
        final LocalDate endDate = LocalDate.now();
        final LocalDate startDate = endDate.minusMonths(11).withDayOfMonth(1);

        // Mapa de tradução imutável (Java 9+)
        final Map<String, String> MESES_TRADUCAO = Map.ofEntries(
                Map.entry("Jan", "Jan"),
                Map.entry("Feb", "Fev"),
                Map.entry("Mar", "Mar"),
                Map.entry("Apr", "Abr"),
                Map.entry("May", "Mai"),
                Map.entry("Jun", "Jun"),
                Map.entry("Jul", "Jul"),
                Map.entry("Aug", "Ago"),
                Map.entry("Sep", "Set"),
                Map.entry("Oct", "Out"),
                Map.entry("Nov", "Nov"),
                Map.entry("Dec", "Dez")
        );

        // Consulta o banco de dados
        List<Object[]> results = repository.countEmprestimosPorMes(startDate, endDate);

        // Cria mapa ordenado por mês
        Map<String, Long> emprestimosPorMes = new LinkedHashMap<>();

        // Inicializa todos os meses com 0 em português
        for (int i = 0; i < 12; i++) {
            String monthKey = endDate.minusMonths(11 - i)
                    .getMonth()
                    .getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            String monthPt = MESES_TRADUCAO.getOrDefault(monthKey, monthKey);
            emprestimosPorMes.put(monthPt, 0L);
        }

        // Preenche com os valores do banco de dados
        results.forEach(result -> {
            if (result != null && result.length == 2) {
                String monthEn = result[0] != null ? (String) result[0] : "";
                Long count = result[1] != null ? ((Number) result[1]).longValue() : 0L;
                String monthPt = MESES_TRADUCAO.getOrDefault(monthEn, monthEn);
                emprestimosPorMes.put(monthPt, count);
            }
        });

        return emprestimosPorMes;
    }
}