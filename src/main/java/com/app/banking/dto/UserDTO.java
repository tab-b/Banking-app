package com.app.banking.dto;

import com.app.banking.model.AppUser;

import java.time.Instant;
import java.util.Set;

public record UserDTO(
        Long id,
        String firstName,
        String lastName,
        String email,
        Set<String> roles,
        Instant createdAt
) {
    public static UserDTO from(AppUser user) {
        return new UserDTO(
                user.getId(),
                user.getfName(),
                user.getlName(),
                user.getEmail(),
                user.getRoles(),
                user.getCreatedAt()
        );
    }
}
