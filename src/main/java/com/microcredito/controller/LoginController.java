// src/main/java/com/microcredito/controller/LoginController.java
package com.microcredito.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login"; // Thymeleaf template for login.html
    }

   // @GetMapping("/principal")
  //  public String principal() {
   //     return "principal"; // Thymeleaf template for main screen
  //  }
}