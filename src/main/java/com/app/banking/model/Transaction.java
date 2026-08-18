package com.app.banking.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String transactionId;

    @Column(nullable = false)
    private String fromId;

    @Column(nullable = false)
    private String toId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private TransactionType type;

    private LocalDateTime timestamp;

    protected Transaction() {}

    public Transaction(String fromId, String toId, BigDecimal amount, TransactionType type) {
        this.fromId = fromId;
        this.toId = toId;
        this.amount = amount;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }
}
