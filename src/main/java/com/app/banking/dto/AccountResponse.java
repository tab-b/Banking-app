package com.app.banking.dto;

import com.app.banking.model.AccountType;
import com.app.banking.model.Status;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountResponse(
    AccountType type,
    String accountNumber,
    Status status,
    Long userId,
    LocalDate openingDate,
    LocalDate closingDate,
    BigDecimal balance

) {
}
