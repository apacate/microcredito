package com.microcredito.controller;

import com.microcredito.entity.Funcionario;
import com.microcredito.service.FuncionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/funcionarios")
public class FuncionarioController {

    @Autowired
    private FuncionarioService service;

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Page<Funcionario> funcionariosPage = service.findAll(PageRequest.of(page, size));
        model.addAttribute("funcionarios", funcionariosPage);
        return "funcionarios";
    }

    @GetMapping("/new")
    public String newFuncionario(Model model) {
        model.addAttribute("funcionario", new Funcionario());
        return "funcionario-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("funcionario") Funcionario funcionario) {
        service.save(funcionario);
        return "redirect:/funcionarios";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("funcionario", service.findById(id).orElseThrow());
        return "funcionario-form";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.deleteById(id);
        return "redirect:/funcionarios";
    }
}