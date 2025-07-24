// src/main/java/com/microcredito/controller/ClienteController.java
package com.microcredito.controller;

import com.microcredito.entity.Cliente;
import com.microcredito.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/clientes")
public class ClienteController {



    @Autowired
    private ClienteService service;



    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Page<Cliente> clientesPage = service.findAll(PageRequest.of(page, size));
        model.addAttribute("clientes", clientesPage);
        return "clientes";
    }

    @GetMapping("/new")
    public String newCliente(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "cliente-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("cliente") Cliente cliente) {
        service.save(cliente);
        return "redirect:/clientes";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("cliente", service.findById(id).orElseThrow());
        return "cliente-form";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.deleteById(id);
        return "redirect:/clientes";
    }


    @GetMapping("/search")
    public String search(
            @RequestParam String termo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Page<Cliente> clientesPage = service.searchByNomeOrSobrenome(termo, PageRequest.of(page, size));
        model.addAttribute("clientes", clientesPage);
        model.addAttribute("termo", termo);
        return "clientes";
    }
}