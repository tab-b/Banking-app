package com.app.banking.services;

import com.app.banking.model.Account;
import com.app.banking.repositories.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {
    private final AccountRepository accountRepo;

    public AccountService(AccountRepository aRepo) {
        accountRepo = aRepo;
    }

    public List<Account> getAccountsForCurrentUser() {
        return accountRepo.findAll();
    }

    public Account getAccount(String accNum) {
        return accountRepo.findById(accNum).orElseThrow();
    }
}
