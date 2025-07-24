// src/main/java/com/microcredito/dto/EmprestimoDto.java
package com.microcredito.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class EmprestimoDto {
    private BigDecimal valorEmprestimo;
    private LocalDate dataContratacao;
    private Long clienteId;
    private String tipoCalculo; // "CONSTANTE" ou "CAPITAL_CONSTANTE"
    private int numeroPrestacoes;
    private BigDecimal taxaJuroMensal;


}
