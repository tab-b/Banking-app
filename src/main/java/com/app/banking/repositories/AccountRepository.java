package com.app.banking.repositories;


import com.app.banking.model.Account;
import com.app.banking.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {

}
