package com.app.banking.controllers;

import com.app.banking.model.Transaction;
import com.app.banking.services.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/account/transfer/")
public class TransactionController {
    private final TransactionService tranService;

    protected TransactionController() {}

    public TransactionController(TransactionService tService) {
        tranService = tService;
    }

    public ResponseEntity<Void> transfer(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam BigDecimal amount) {
        tranService.transfer(from, to, amount);
        return ResponseEntity.ok().build();
    }
}
