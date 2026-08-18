package com.app.banking.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Account {
    private AccountType type;
    private String accountNumber;
    private Status status;
    private Long customerId;
    private LocalDate openingDate;
    private LocalDate closingDate;
    private BigDecimal balance;

    protected Account() {}
    public Account(BigDecimal balance, AccountType type, String accountNumber, Status status) {
        this.balance = balance;
        this.type = type;
        this.accountNumber = accountNumber;
        this.status = status;
    }

    public AccountType getType() {
        return type;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Status getStatus() {
        return status;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public LocalDate getOpeningDate() {
        return openingDate;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void withdraw(BigDecimal amount) {
        balance = balance.subtract(amount);
    }

    public void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }

}
