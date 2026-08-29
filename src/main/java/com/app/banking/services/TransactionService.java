package com.app.banking.services;

import com.app.banking.dto.TransferRequest;
import com.app.banking.exceptions.*;
import com.app.banking.model.*;
import com.app.banking.repositories.AccountRepository;
import com.app.banking.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionService {
    private final AccountService accountService;
    private final TransactionRepository tranRepo;

    public TransactionService(TransactionRepository tRepo, AccountService accountService) {
        tranRepo = tRepo;
        this.accountService = accountService;
    }

    public void validateTransfer(TransferRequest transfer) {
        Optional<Transaction> existing = tranRepo.findByIdempotencyKey(transfer.idempotencyKey());
        if(existing.isPresent()) {
            Transaction existingTransaction = existing.get();
            if(existingTransaction.getFromAccountNum().equals(transfer.fromAccountNumber()) == false ||
                    existingTransaction.getToAccountNum().equals(transfer.toAccountNumber()) == false) {
                throw new IdempotencyKeyReuseException();
            }
        }
    }

    @Transactional
    public Transaction transfer(TransferRequest transaction) {
        validateTransfer(transaction);

        BigDecimal amount = transaction.amount();
        String fromAccountNumber = transaction.fromAccountNumber();
        String toAccountNumber = transaction.toAccountNumber();


        if(amount.compareTo(BigDecimal.ZERO) <= 0) throw new InvalidTransferException("Amount cannot be less than or equal to zero!");
        if(fromAccountNumber.equals(toAccountNumber)) throw new InvalidTransferException("Cannot transfer to the same account!");
        Account from = accountService.getCurrentUserAccount(fromAccountNumber);
        Account to = accountService.getAccountByNumber(toAccountNumber);

        if(from.isActive() == false) throw new AccountNotActiveException(fromAccountNumber);
        if(to.isActive() == false) throw new AccountNotActiveException(toAccountNumber);

        if(from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }

        // DO the transfer
        from.withdraw(amount);
        to.deposit(amount);
//        accountRepo.save(from);
//        accountRepo.save(to);

        // Record transactions
        UUID transactionID = UUID.randomUUID();
        Transaction fromTransaction = new Transaction(transactionID, transaction.idempotencyKey(), fromAccountNumber, toAccountNumber, amount.negate(), TransactionType.TRANSFER);
        Transaction toTransaction = new Transaction(transactionID, transaction.idempotencyKey(), fromAccountNumber, toAccountNumber, amount, TransactionType.TRANSFER);
        tranRepo.saveAll(List.of(fromTransaction, toTransaction));

        return fromTransaction;
    }
}
