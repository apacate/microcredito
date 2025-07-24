// src/main/java/com/microcredito/repository/ClienteRepository.java
package com.microcredito.repository;

import com.microcredito.entity.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Existing non-paginated search with @Query
    @Query("SELECT c FROM Cliente c WHERE c.nome LIKE %:termo% OR c.sobrenome LIKE %:termo%")
    List<Cliente> findByNomeOrSobrenome(@Param("termo") String termo);

    // Paginated version of the same query
    @Query("SELECT c FROM Cliente c WHERE c.nome LIKE %:termo% OR c.sobrenome LIKE %:termo%")
    Page<Cliente> findByNomeOrSobrenome(@Param("termo") String termo, Pageable pageable);

    // Additional query methods using Spring Data JPA naming convention
    Page<Cliente> findByNomeContainingIgnoreCaseOrSobrenomeContainingIgnoreCase(
            String nome, String sobrenome, Pageable pageable);

    List<Cliente> findByNomeContainingIgnoreCaseOrSobrenomeContainingIgnoreCase(
            String nome, String sobrenome);
}