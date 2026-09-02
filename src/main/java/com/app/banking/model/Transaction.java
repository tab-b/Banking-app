package com.app.banking.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID transactionId;

    @Column(nullable = false, updatable = false)
    private String fromAccountNum;

    @Column(nullable = false, updatable = false)
    private String toAccountNum;

    @Column(nullable = false, updatable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private TransactionType type;

    @Column(nullable = false, updatable = false)
    private Instant timestamp;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID idempotencyKey;

    protected Transaction() {}

    public Transaction(UUID transactionId, UUID idempotencyKey, String fromId, String toId, BigDecimal amount, TransactionType type) {
        this.transactionId = transactionId;
        this.fromAccountNum = fromId;
        this.toAccountNum = toId;
        this.amount = amount;
        this.type = type;
        this.timestamp = Instant.now();
        this.idempotencyKey = idempotencyKey;
    }

    public Long getId() {
        return id;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public String getFromAccountNum() {
        return fromAccountNum;
    }

    public String getToAccountNum() {
        return toAccountNum;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public UUID getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(UUID idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }
}
