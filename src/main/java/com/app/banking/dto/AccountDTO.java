package com.app.banking.dto;

import com.app.banking.model.Account;
import com.app.banking.model.AccountType;
import com.app.banking.model.Status;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountDTO(
    AccountType type,
    String accountNumber,
    Status status,
    LocalDate openingDate,
    LocalDate closingDate,
    BigDecimal balance

) {
    public static AccountDTO from(Account account) {
        return new AccountDTO(
                account.getType(),
                account.getAccountNumber(),
                account.getStatus(),
                account.getOpeningDate(),
                account.getClosingDate(),
                account.getBalance()
        );
    }
}
