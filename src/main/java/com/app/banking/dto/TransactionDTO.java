package com.app.banking.dto;

import com.app.banking.model.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionDTO(
        BigDecimal amount,
        UUID transactionId,
        TransactionType type,
        String receiverFirstName,
        String receiverLastName,
        Instant timestamp
) {
}
