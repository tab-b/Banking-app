package com.app.banking;

import com.app.banking.dto.TransferRequest;
import com.app.banking.exceptions.IdempotencyKeyReuseException;
import com.app.banking.exceptions.InsufficientFundsException;
import com.app.banking.model.Account;
import com.app.banking.model.AccountType;
import com.app.banking.model.AppUser;
import com.app.banking.model.Transaction;
import com.app.banking.repositories.AccountRepository;
import com.app.banking.repositories.TransactionRepository;
import com.app.banking.repositories.UserRepository;
import com.app.banking.services.AccountNumberService;
import com.app.banking.services.TransactionService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
public class TransferTests {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountNumberService accountNumberService;

    @Autowired
    private UserRepository userRepository;

    private AppUser user;
    private AppUser recipientUser;
    private AppUser recipientUser2;

    private Account checking;
    private Account checking2;
    private Account recipient;
    private Account recipient2;

    void setupData() {
        var u = new AppUser(
                "john",
                "doe",
                "test@gmail.com",
                Set.of("ROLE_USER"),
                "password12345"
        );
        var r1 = new AppUser(
                "bob",
                "joe",
                "bobjoe@gmail.com",
                Set.of("ROLE_USER"),
                "bobberjoe3456"
        );

        var r2 = new AppUser(
                "tilly",
                "billy",
                "thebigtilly@yahoo.com",
                Set.of("ROLE_USER"),
                "tillyjillybilly1234"
        );

        user = userRepository.save(u);
        recipientUser = userRepository.save(r1);
        recipientUser2 = userRepository.save(r2);

        checking = new Account(
                AccountType.CHECKING,
                accountNumberService.generateUnique(),
                user.getId()
        );

        checking2 = new Account(
                AccountType.CHECKING,
                accountNumberService.generateUnique(),
                user.getId()
        );

        recipient = new Account(
                AccountType.CHECKING,
                accountNumberService.generateUnique(),
                recipientUser.getId()
        );
        recipient2 = new Account(
                AccountType.CHECKING,
                accountNumberService.generateUnique(),
                recipientUser2.getId()
        );

        checking.setBalance(new BigDecimal("1000.00"));
        checking2.setBalance(new BigDecimal("1000.00"));

        recipient.setBalance(new BigDecimal("500.00"));
        recipient2.setBalance(new BigDecimal("500.00"));

        accountRepository.saveAll(
                List.of(
                        checking,
                        checking2,
                        recipient,
                        recipient2
                )
        );

    }

    @BeforeEach
    void reset() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();

        setupData();
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void insufficientFundsShouldThrow() {
        UUID key = UUID.randomUUID();
        TransferRequest request = new TransferRequest(
                checking.getAccountNumber(),
                recipient.getAccountNumber(),
                new BigDecimal("2000.00"),
                key
        );
        assertThrows(InsufficientFundsException.class, () -> transactionService.transfer(request, user.getId()));

    }

    // same key + same request
    // -> same transaction ID
    // -> balances changed exactly once
    @Test
    @WithMockUser(username = "test@gmail.com")
    void sameRequestWithSameIdempotencyKeyShouldOnlyTransferOnce() {
        UUID key = UUID.randomUUID();
        TransferRequest request = new TransferRequest(
                checking.getAccountNumber(),
                recipient.getAccountNumber(),
                new BigDecimal("100.00"),
                key
        );

        Transaction first = transactionService.transfer(request, user.getId());
        Transaction second = transactionService.transfer(request, user.getId());

        Account updatedChecking =
                accountRepository.findById(checking.getId()).orElseThrow();
        Account updatedRecipient =
                accountRepository.findById(recipient.getId()).orElseThrow();

        assertEquals(first.getTransactionId(), second.getTransactionId());
        assertEquals(
                0,
                updatedChecking.getBalance().compareTo(new BigDecimal("900.00"))
        );
        assertEquals(
                0,
                updatedRecipient.getBalance().compareTo(new BigDecimal("600.00"))
        );
        assertEquals(1, transactionRepository.count());
    }

    // same key + different amount
    // -> IdempotencyKeyReuseException
    @Test
    @WithMockUser(username = "test@gmail.com")
    void sameIdempotencyKeyWithDifferentAmountShouldThrow() {
        UUID key = UUID.randomUUID();
        TransferRequest request1 = new TransferRequest(
                checking.getAccountNumber(),
                recipient.getAccountNumber(),
                new BigDecimal("100.00"),
                key
        );
        TransferRequest request2 = new TransferRequest(
                checking.getAccountNumber(),
                recipient.getAccountNumber(),
                new BigDecimal("200.00"),
                key
        );
        transactionService.transfer(request1, user.getId());
        assertThrows(IdempotencyKeyReuseException.class, () -> transactionService.transfer(request2, user.getId()));
        Account updatedChecking = accountRepository.findById(checking.getId()).orElseThrow();
        Account updatedRecipient = accountRepository.findById(recipient.getId()).orElseThrow();

        assertEquals(0, updatedChecking.getBalance().compareTo(new BigDecimal("900.00")));
        assertEquals(0, updatedRecipient.getBalance().compareTo(new BigDecimal("600.00")));
        assertEquals(1, transactionRepository.count());



    }

    // same key + different destination
    // -> IdempotencyKeyReuseException
    @Test
    @WithMockUser(username = "test@gmail.com")
    void sameKeyDifferentDestinationShouldThrow() {
        UUID key = UUID.randomUUID();
        TransferRequest request1 = new TransferRequest(
                checking.getAccountNumber(),
                recipient.getAccountNumber(),
                new BigDecimal("100.00"),
                key
        );
        TransferRequest request2 = new TransferRequest(
                checking.getAccountNumber(),
                recipient2.getAccountNumber(),
                new BigDecimal("100.00"),
                key
        );
        Transaction transfer = transactionService.transfer(request1, user.getId());
        assertThrows(IdempotencyKeyReuseException.class, () -> transactionService.transfer(request2, user.getId()));
        assertEquals(1, transactionRepository.count());
    }


    // same key + different source
    // -> IdempotencyKeyReuseException
    @Test
    @WithMockUser(username = "test@gmail.com")
    void sameKeyDifferentSourcesShouldThrow() {
        UUID key = UUID.randomUUID();
        TransferRequest request1 = new TransferRequest(
                checking.getAccountNumber(),
                recipient.getAccountNumber(),
                new BigDecimal("100.00"),
                key
        );
        TransferRequest request2 = new TransferRequest(
                checking2.getAccountNumber(),
                recipient.getAccountNumber(),
                new BigDecimal("200.00"),
                key
        );

        Transaction transfer = transactionService.transfer(request1, user.getId());
        assertThrows(IdempotencyKeyReuseException.class, () -> transactionService.transfer(request2, user.getId()));
        assertEquals(1, transactionRepository.count());
    }


    // different keys + same request
    // -> two independent transfers
    @Test
    @WithMockUser(username = "test@gmail.com")
    void differentKeysSameTransferShouldExecuteTwice() {
        UUID key = UUID.randomUUID();
        UUID key2 = UUID.randomUUID();

        TransferRequest request1 = new TransferRequest(
                checking.getAccountNumber(),
                recipient.getAccountNumber(),
                new BigDecimal("100.00"),
                key
        );
        TransferRequest request2 = new TransferRequest(
                checking.getAccountNumber(),
                recipient.getAccountNumber(),
                new BigDecimal("100.00"),
                key2
        );

        Transaction transfer = transactionService.transfer(request1, user.getId());
        Transaction transfer2 = transactionService.transfer(request2, user.getId());
        assertNotEquals(transfer.getTransactionId(), transfer2.getTransactionId());

        Account updatedChecking = accountRepository.findById(checking.getId()).orElseThrow();
        Account updatedRecipient = accountRepository.findById(recipient.getId()).orElseThrow();

        assertEquals(0, updatedChecking.getBalance().compareTo(new BigDecimal("800.00")));
        assertEquals(0, updatedRecipient.getBalance().compareTo(new BigDecimal("700.00")));

        assertEquals(2, transactionRepository.count());
    }

    // simultaneous requests with same key
    // -> only one balance change
    // -> only one Transaction row
    @Test
    @WithMockUser(username = "test@gmail.com")
    void concurrentDuplicateTransferShouldNotDoubleSpend() throws Exception{
        UUID key = UUID.randomUUID();
        TransferRequest request = new TransferRequest(
                checking.getAccountNumber(),
                recipient.getAccountNumber(),
                new BigDecimal("100.00"),
                key
        );

        ExecutorService executor = new DelegatingSecurityContextExecutorService(Executors.newFixedThreadPool(2));
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        Runnable task = () -> {
            ready.countDown();
            try {
                start.await();

                transactionService.transfer(
                        request,
                        user.getId()
                );
            } catch(Exception ignored) {
            }
        };

        Future<?> first = executor.submit(task);
        Future<?> second = executor.submit(task);

        ready.await();

        start.countDown();

        first.get();
        second.get();

        executor.shutdown();

        Account from = accountRepository.findById(checking.getId()).orElseThrow();
        Account to = accountRepository.findById(recipient.getId()).orElseThrow();
        System.out.println(from.getBalance());
        System.out.println(to.getBalance());
        assertEquals(0, from.getBalance().compareTo(new BigDecimal("900.00")));
        assertEquals(0, to.getBalance().compareTo(new BigDecimal("600.00")));
        assertEquals(1, transactionRepository.count());

        assertTrue(transactionRepository.findByIdempotencyKey(key).isPresent());
    }
}
