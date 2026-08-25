package com.app.banking.services;

import com.app.banking.dto.CreateAccountRequest;
import com.app.banking.model.Account;
import com.app.banking.model.AppUser;
import com.app.banking.repositories.AccountRepository;
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

        return accountRepo.save(account);
    }

    public List<Account> getAccountsForCurrentUser() {
        AppUser currentUser = userService.getCurrentUser();
        return accountRepo.findByOwner(currentUser);
    }

    public Account getAccount(String accNum) {
        AppUser currentUser = userService.getCurrentUser();

        return accountRepo.findByAccountNumberAndOwner(accNum, currentUser).orElseThrow();
    }
}
