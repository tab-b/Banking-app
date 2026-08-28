package com.app.banking.exceptions;

public record ErrorResponse(
        int status,
        String message
) {
}
