package com.app.banking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
public class BankingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankingApplication.class, args);
    }
    @GetMapping("/")
    public String helloWorld() {
        return "Hello world!";
    }

}

