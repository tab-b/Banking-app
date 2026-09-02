package com.app.banking.model;

import com.app.banking.exceptions.AccountNotActiveException;
import com.app.banking.exceptions.InsufficientFundsException;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    @Column(nullable = false, unique = true)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false, updatable = false)
    private Long ownerId;

    @Column(nullable = false)
    private LocalDate openingDate;

    private LocalDate closingDate;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Version
    private Long version;

    protected Account() {}

    public Account(AccountType type, String accountNumber, Long ownerId) {
        this.balance = BigDecimal.ZERO;
        this.type = type;
        this.accountNumber = accountNumber;
        this.ownerId = ownerId;
        status = Status.ACTIVE;
        openingDate = LocalDate.now();

    }

    public Long getId() {
        return id;
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

    public Long getOwnerId() {
        return ownerId;
    }

    public LocalDate getOpeningDate() {
        return openingDate;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void withdraw(BigDecimal amount) {
        if(amount == null || amount.signum() <= 0) throw new IllegalArgumentException("Withdrawal amount must be positive");
        if(!isActive()) throw new AccountNotActiveException(accountNumber);
        if(balance.compareTo(amount) < 0) throw new InsufficientFundsException();

        balance = balance.subtract(amount);
    }

    public void deposit(BigDecimal amount) {
        if(!isActive()) throw new AccountNotActiveException(accountNumber);
        if(amount == null || amount.signum() <= 0) throw new IllegalArgumentException("Deposit amount must be positive!");

        balance = balance.add(amount);
    }
    public void close() {
        if(status == Status.CLOSED) return;

        status = Status.CLOSED;
        closingDate = LocalDate.now();
    }

    public void suspend() {
        if(status == Status.CLOSED) throw new IllegalStateException("Closed account cannot be suspended");

        status = Status.SUSPENDED;
    }

    public void activate() {
        if(status == Status.CLOSED) throw new IllegalStateException("Closed account cannot be activated");

        status = Status.ACTIVE;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
