package com.app.banking.dto;

import com.app.banking.model.AppUser;

import java.time.Instant;
import java.util.Set;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        Set<String> roles,
        Instant createdAt
) {
    public static UserResponse from(AppUser user) {
        return new UserResponse(
                user.getId(),
                user.getfName(),
                user.getlName(),
                user.getEmail(),
                user.getRoles(),
                user.getCreatedAt()
        );
    }
}
