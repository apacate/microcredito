package com.microcredito;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class MicrocreditoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicrocreditoApplication.class, args);
    }
}
