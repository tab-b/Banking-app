package com.app.banking.services;

import com.app.banking.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class AccountNumberService {
    private final SecureRandom random = new SecureRandom();
    private final AccountRepository accountRepo;

    public AccountNumberService(AccountRepository aRepo){
        accountRepo = aRepo;
    }

    public String generate() {
        long number = 1_000_000_000L +
                random.nextLong(9_000_000_000L);

        return String.valueOf(number);
    }

    public String generateUnique() {
        String accountNumber;
        do {
            accountNumber = generate();
        }while(accountRepo.existsByAccountNumber(accountNumber) == true);
        return accountNumber;
    }
}
