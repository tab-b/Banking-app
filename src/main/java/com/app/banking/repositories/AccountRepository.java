package com.app.banking.repositories;


import com.app.banking.model.Account;
import com.app.banking.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByAccountNumberAndOwner(String accountNum, AppUser owner);
    List<Account> findByOwner(AppUser owner);
    List<Account> findByOwnerId(Long userId);
    Optional<Account> findByAccountNumber(String accountNum);
    boolean existsByAccountNumber(String accountNumber);

}
