package com.app.banking.dto;

import com.app.banking.model.AccountType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAccountRequest(
    @NotNull(message = "Type of account is required")
    AccountType type
) {
}
