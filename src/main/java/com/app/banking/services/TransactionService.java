package com.app.banking.services;

import com.app.banking.model.Account;
import com.app.banking.model.Transaction;
import com.app.banking.model.TransactionType;
import com.app.banking.repositories.AccountRepository;
import com.app.banking.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;

@Service
public class TransactionService {
    @Autowired private AccountRepository accountRepo;
    @Autowired private TransactionRepository tranRepo;
    @Transactional
    public void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        Account from = accountRepo.findById(fromAccountNumber).orElseThrow();
        Account to = accountRepo.findById(toAccountNumber).orElseThrow();

        if(from.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Not enough funds"); // change this later to custom insufficientfunds exception
        }

        // DO the transfer
        from.withdraw(amount);
        to.deposit(amount);
        accountRepo.save(from);
        accountRepo.save(to);

        // Record transactions
        Transaction fromTransaction = new Transaction(fromAccountNumber, toAccountNumber, amount.negate(), TransactionType.TRANSFER);
        Transaction toTransaction = new Transaction(fromAccountNumber, toAccountNumber, amount, TransactionType.TRANSFER);
        tranRepo.saveAll(Arrays.asList(fromTransaction, toTransaction));
    }
}
