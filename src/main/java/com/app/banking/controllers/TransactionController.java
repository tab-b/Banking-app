package com.app.banking.controllers;

import com.app.banking.dto.TransferRequest;
import com.app.banking.model.Transaction;
import com.app.banking.repositories.TransactionRepository;
import com.app.banking.security.CustomUserDetails;
import com.app.banking.services.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/account/transfer/")
public class TransactionController {
    private final TransactionService tranService;

    public TransactionController(TransactionService tService) {
        tranService = tService;
    }

    public ResponseEntity<Void> transfer(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody TransferRequest request) {
        tranService.transfer(request, currentUser.getId());
        return ResponseEntity.ok().build();
    }
}
