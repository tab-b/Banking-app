package com.app.banking.dto;

public record AccountsChangedEvent(
        Long fromOwnerId,
        Long toOwnerId
) {
}
