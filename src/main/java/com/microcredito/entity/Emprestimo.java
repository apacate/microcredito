// src/main/java/com/microcredito/entity/Emprestimo.java
package com.microcredito.entity;

import com.microcredito.enums.StatusEmprestimo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name = "tb_emprestimos")
@Data
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Cliente cliente;

    private BigDecimal valorEmprestimo;

    private LocalDate dataContratacao;

    private String tipoAmortizacao;

    private int numeroParcelas;

    private BigDecimal taxaJurosAnual;

    private String frequenciaPagamento;

    @Enumerated(EnumType.STRING)
    private StatusEmprestimo status;

    @OneToMany(mappedBy = "emprestimo", cascade = CascadeType.ALL)
    private List<Parcela> parcelas;
}