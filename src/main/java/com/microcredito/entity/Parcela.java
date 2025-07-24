// src/main/java/com/microcredito/entity/Parcela.java
package com.microcredito.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter

@Table(name = "tb_parcelas")
@Data
public class Parcela {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer numeroParcela;

    private LocalDate dataVencimento;

    private BigDecimal valorAmortizacao;

    private BigDecimal valorJuros;

    private BigDecimal valorPrestacao;

    private String status;

    @ManyToOne
    @JoinColumn(name = "emprestimo_id")
    private Emprestimo emprestimo;
}