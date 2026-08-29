package com.app.banking.exceptions;

public class IdempotencyKeyReuseException extends RuntimeException {
    public IdempotencyKeyReuseException() {
        super("Idempotency key already used!");
    }
}
