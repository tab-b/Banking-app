package com.app.banking.controllers;

import com.app.banking.dto.AccountDTO;
import com.app.banking.dto.CreateAccountRequest;
import com.app.banking.model.Account;
import com.app.banking.services.AccountService;
import com.app.banking.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService aServ) {
        accountService = aServ;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public AccountDTO createAccount(@Valid @RequestBody CreateAccountRequest request) {
        return AccountDTO.from(
                accountService.createAccount(request)
        );
    }

    @GetMapping
    public List<Account> getAllAccounts() {
        return accountService.getAccountsForCurrentUser();
    }

    @GetMapping("/{accountNumber}")
    public Account getAccount(@PathVariable String accountNumber) {
        return accountService.getAccount(accountNumber);
    }
}
