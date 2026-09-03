package com.app.banking.services;

import com.app.banking.dto.TransferRequest;
import com.app.banking.exceptions.*;
import com.app.banking.model.*;
import com.app.banking.repositories.AccountRepository;
import com.app.banking.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
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

    public Optional<Transaction> findExistingTransaction(TransferRequest request) {
        Optional<Transaction> existing = tranRepo.findByIdempotencyKey(request.idempotencyKey());
        if(existing.isEmpty()) return Optional.empty();

        Transaction transaction = existing.get();
        boolean sameRequest =
                transaction.getFromAccountNum().equals(request.fromAccountNumber()) &&
                        transaction.getToAccountNum().equals(request.toAccountNumber()) &&
                        transaction.getAmount().abs().compareTo(request.amount()) == 0;

        if(sameRequest == false) {
            throw new IdempotencyKeyReuseException();
        }
        return Optional.of(transaction);
    }

    @Transactional
    public Transaction transfer(TransferRequest transaction, Long currentUserId) {
        Optional<Transaction> existingTransfer =  findExistingTransaction(transaction);
        if(existingTransfer.isPresent()) return existingTransfer.get();

        BigDecimal amount = transaction.amount();
        String fromAccountNumber = transaction.fromAccountNumber();
        String toAccountNumber = transaction.toAccountNumber();

        if(fromAccountNumber.equals(toAccountNumber)) throw new InvalidTransferException("Cannot transfer to the same account!");
        if(amount.compareTo(BigDecimal.ZERO) <= 0) throw new InvalidTransferException("Amount cannot be less than or equal to zero!");

        Account from = accountService.getCurrentUserAccount(currentUserId, fromAccountNumber);
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
        Transaction transferRecord = new Transaction(transactionID, transaction.idempotencyKey(), fromAccountNumber, toAccountNumber, amount, TransactionType.TRANSFER);
//        Transaction toTransaction = new Transaction(transactionID, transaction.idempotencyKey(), fromAccountNumber, toAccountNumber, amount, TransactionType.TRANSFER);
        tranRepo.save(transferRecord);

        return transferRecord;
    }
}
