package com.microcredito.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/configuracoes")
public class ConfiguracoesController {

    @GetMapping
    public String showConfiguracoes() {
        return "configuracoes"; // Nome do template Thymeleaf
    }
}