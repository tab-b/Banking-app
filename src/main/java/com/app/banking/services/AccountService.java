package com.app.banking.services;

import com.app.banking.dto.CreateAccountRequest;
import com.app.banking.exceptions.AccountNotFoundException;
import com.app.banking.model.Account;
import com.app.banking.model.AppUser;
import com.app.banking.repositories.AccountRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {
    private final AccountRepository accountRepo;
    private final UserService userService;
    private final AccountNumberService accountNumberServ;

    public AccountService(AccountRepository aRepo, UserService uServ, AccountNumberService accNumServ) {
        accountRepo = aRepo;
        userService = uServ;
        accountNumberServ = accNumServ;

    }

    public Account createAccount(CreateAccountRequest request) {
        AppUser currentUser = userService.getCurrentUser();
        String accountNumber = accountNumberServ.generateUnique();
        Account account = new Account(
                request.type(),
                accountNumber,
                currentUser
        );

        currentUser.addAccount(account);
        return accountRepo.save(account);
    }

    @PreAuthorize("isAuthenticated()")
    public List<Account> getAccountsForCurrentUser() {
        AppUser currentUser = userService.getCurrentUser();
        return accountRepo.findByOwner(currentUser);
    }

    @PreAuthorize("isAuthenticated()")
    public Account getCurrentUserAccount(String accNum) {
        AppUser currentUser = userService.getCurrentUser();
        return accountRepo.findByAccountNumberAndOwner(accNum, currentUser).orElseThrow(() -> new AccountNotFoundException(accNum));
    }

    public Account getAccountByNumber(String accountNumber) {
        return accountRepo
                .findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }
}
