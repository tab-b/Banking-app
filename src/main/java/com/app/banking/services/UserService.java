package com.app.banking.services;

import com.app.banking.dto.CreateUserRequest;
import com.app.banking.model.AppUser;
import com.app.banking.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    public Optional<AppUser> getUserByEmail(String email) {
        return userRepo.getUserByEmail(email);
    }

    public AppUser createUser(CreateUserRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        if(userRepo.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Account with this email already exists");
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
