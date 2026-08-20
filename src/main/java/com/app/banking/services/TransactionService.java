package com.app.banking.services;

import com.app.banking.exceptions.AccountNotFoundException;
import com.app.banking.exceptions.InsufficientFundsException;
import com.app.banking.exceptions.InvalidTransferException;
import com.app.banking.model.*;
import com.app.banking.repositories.AccountRepository;
import com.app.banking.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.channels.AcceptPendingException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionService {
    private final AccountRepository accountRepo;
    private final TransactionRepository tranRepo;

    public TransactionService(TransactionRepository tRepo, AccountRepository aRepo) {
        tranRepo = tRepo;
        accountRepo = aRepo;
    }

    @Transactional
    public void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        if(amount.compareTo(BigDecimal.valueOf(0)) <= 0) throw new InvalidTransferException("Amount cannot be less than or equal to zero!");
        if(fromAccountNumber.equals(toAccountNumber)) throw new InvalidTransferException("Cannot transfer to the same account!");
        Account from = accountRepo.findById(fromAccountNumber).orElseThrow(() -> new AccountNotFoundException(fromAccountNumber));
        Account to = accountRepo.findById(toAccountNumber).orElseThrow( () -> new AccountNotFoundException(toAccountNumber));

        if(from.isActive() == false) throw new AccountNotFoundException(fromAccountNumber);
        if(to.isActive() == false) throw new AccountNotFoundException(toAccountNumber);

        if(from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }

        // DO the transfer
        from.withdraw(amount);
        to.deposit(amount);
        accountRepo.save(from);
        accountRepo.save(to);

        // Record transactions
        UUID transactionID = UUID.randomUUID();
        Transaction fromTransaction = new Transaction(transactionID, fromAccountNumber, toAccountNumber, amount.negate(), TransactionType.TRANSFER);
        Transaction toTransaction = new Transaction(transactionID, fromAccountNumber, toAccountNumber, amount, TransactionType.TRANSFER);
        tranRepo.saveAll(Arrays.asList(fromTransaction, toTransaction));
    }
}
