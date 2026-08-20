package com.app.banking.model;

import com.app.banking.exceptions.AccountNotActiveException;
import com.app.banking.exceptions.InsufficientFundsException;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Account {
    private AccountType type;
    private String accountNumber;
    private Status status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser owner;

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

    public boolean isActive() {
        return status == Status.ACTIVE;
    }

    public AppUser getOwner() {
        return owner;
    }

    public LocalDate getOpeningDate() {
        return openingDate;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void withdraw(BigDecimal amount) {
        if(isActive() == false) throw new AccountNotActiveException(accountNumber);
        if(balance.compareTo(amount) < 0) throw new InsufficientFundsException();
        balance = balance.subtract(amount);
    }

    public void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }

}
