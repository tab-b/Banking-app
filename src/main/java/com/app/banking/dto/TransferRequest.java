package com.app.banking.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequest(
        @NotBlank(message = "From account required")
        String fromAccountNumber,

        @NotBlank(message = "To account required")
        String toAccountNumber,

        @NotNull(message = "Transfer amount is required")
        @DecimalMin(value = "0.01", message = "Transfer amount must be greater than zero")
        BigDecimal amount

) {
}
