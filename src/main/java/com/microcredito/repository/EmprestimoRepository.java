package com.microcredito.repository;

import com.microcredito.entity.Emprestimo;
import com.microcredito.enums.StatusEmprestimo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    long countByDataContratacaoBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT e FROM Emprestimo e WHERE " +
            "(:status IS NULL OR e.status = :status) AND " +
            "(:clienteId IS NULL OR e.cliente.id = :clienteId)")
    Page<Emprestimo> findFiltrado(@Param("status") StatusEmprestimo status,
                                  @Param("clienteId") Long clienteId,
                                  Pageable pageable);

    @Query(value = "SELECT DATE_FORMAT(e.data_contratacao, '%b') as mes, COUNT(e.id) as total " +
            "FROM tb_emprestimos e " +
            "WHERE e.data_contratacao BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE_FORMAT(e.data_contratacao, '%b'), DATE_FORMAT(e.data_contratacao, '%m') " +
            "ORDER BY DATE_FORMAT(e.data_contratacao, '%m')", nativeQuery = true)
    List<Object[]> countEmprestimosPorMes(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);
}