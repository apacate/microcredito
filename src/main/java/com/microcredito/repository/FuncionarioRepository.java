// src/main/java/com/microcredito/repository/FuncionarioRepository.java
package com.microcredito.repository;

import com.microcredito.entity.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    Optional<Funcionario> findByUsuario(String usuario);
}