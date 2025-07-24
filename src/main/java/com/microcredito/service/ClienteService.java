// src/main/java/com/microcredito/service/ClienteService.java
package com.microcredito.service;

import com.microcredito.entity.Cliente;
import com.microcredito.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    // Find all clients with pagination
    public Page<Cliente> findAll(Pageable pageable) {
        return clienteRepository.findAll(pageable);
    }

    // Find all clients without pagination (for cases where you need the complete list)
    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> findById(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente save(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public void deleteById(Long id) {
        clienteRepository.deleteById(id);
    }

    // Search with pagination
    public Page<Cliente> searchByNomeOrSobrenome(String termo, Pageable pageable) {
        return clienteRepository.findByNomeContainingIgnoreCaseOrSobrenomeContainingIgnoreCase(termo, termo, pageable);
    }

    // Search without pagination (for cases where you need the complete list)
    public List<Cliente> searchByNomeOrSobrenome(String termo) {
        return clienteRepository.findByNomeContainingIgnoreCaseOrSobrenomeContainingIgnoreCase(termo, termo);
    }
}