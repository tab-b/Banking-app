package com.app.banking.services;

import com.app.banking.dto.CreateUserRequest;
import com.app.banking.exceptions.EmailAlreadyExists;
import com.app.banking.model.Account;
import com.app.banking.model.AppUser;
import com.app.banking.repositories.UserRepository;
import com.app.banking.security.CustomUserDetails;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }
    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(!(auth.getPrincipal() instanceof CustomUserDetails principal)) throw new IllegalStateException("User is not authenticated");
        return principal.getId();
    }

    public AppUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails principal)) throw new IllegalStateException("User is not authenticated");

        return userRepo.findById(principal.getId())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
    }

    public Optional<AppUser> getUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public AppUser createUser(CreateUserRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        if(userRepo.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyExists("Account with this email already exists!");
        }
        AppUser newUser = new AppUser(
                request.firstName().trim(),
                request.lastName().trim(),
                normalizedEmail,
                Set.of("ROLE_USER"),
                passwordEncoder.encode(request.password())
        );
        return userRepo.save(newUser);
    }
}
