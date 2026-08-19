package com.app.banking.exceptions;

public class AccountNotActiveException extends RuntimeException {
    public AccountNotActiveException(String accountNumber) {
        super("Account " + accountNumber + "is not active!");
    }
}
