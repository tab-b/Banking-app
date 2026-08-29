package com.app.banking.repositories;

import com.app.banking.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByTransactionId(UUID transactionId);

    Optional<Transaction> findByIdempotencyKey(UUID idempotencyKey);
}
