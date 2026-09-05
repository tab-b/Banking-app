package com.app.banking.services;

import com.app.banking.dto.AccountDTO;
import com.app.banking.dto.CreateAccountRequest;
import com.app.banking.exceptions.AccountNotFoundException;
import com.app.banking.model.Account;
import com.app.banking.model.AppUser;
import com.app.banking.repositories.AccountRepository;
import com.app.banking.security.CustomUserDetails;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
        Long currentUserId = userService.getCurrentUser().getId();
        String accountNumber = accountNumberServ.generateUnique();
        Account account = new Account(
                request.type(),
                accountNumber,
                currentUserId
        );
        return accountRepo.save(account);
    }

    @PreAuthorize("isAuthenticated()")
    public List<Account> getAccountEntitiesForCurrentUser(Long currentUserId) {
        return accountRepo.findByOwnerId(currentUserId);
    }

    @Cacheable(
            value = "userAccounts",
            key = "#currentUserId"
    )
    public List<AccountDTO> getAccountsDTOForCurrentUser(Long currentUserId) {
        return accountRepo.findByOwnerId(currentUserId)
                .stream()
                .map(AccountDTO::from)
                .toList();
    }

    @PreAuthorize("isAuthenticated()")
    public Account getCurrentUserAccount(Long currentUserId, String accNum) {
        return accountRepo.findByAccountNumberAndOwnerId(accNum, currentUserId).orElseThrow(() -> new AccountNotFoundException(accNum));
    }

    public Account getAccountByNumber(String accountNumber) {
        return accountRepo
                .findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }
}
