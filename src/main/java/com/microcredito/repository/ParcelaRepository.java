// src/main/java/com/microcredito/repository/ParcelaRepository.java
package com.microcredito.repository;

import com.microcredito.entity.Parcela;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParcelaRepository extends JpaRepository<Parcela, Long> {
    List<Parcela> findByEmprestimoId(Long emprestimoId);
    long countByStatus(String status);

    @Query("SELECT p FROM Parcela p WHERE p.status = :status")
    List<Parcela> findByStatus(@Param("status") String status);
}