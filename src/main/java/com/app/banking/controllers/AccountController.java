package com.app.banking.controllers;

import com.app.banking.model.Account;
import com.app.banking.services.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/accounts/")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService aServ) {
        accountService = aServ;
    }

    @GetMapping
    public List<Account> getAccounts() {
        return accountService.getAccountsForCurrentUser();
    }

    @GetMapping("/{accountNumber}")
    public Account getAccount(@PathVariable String accountNumber) {
        return accountService.getAccount(accountNumber);
    }

}
