package com.app.banking.controllers;
import com.app.banking.dto.CreateUserRequest;
import com.app.banking.dto.LoginRequest;
import com.app.banking.dto.UserResponse;
import com.app.banking.model.AppUser;
import com.app.banking.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userServ;
    private final AuthenticationManager authManager;

    public AuthController(UserService u, AuthenticationManager aManager) {
        userServ = u;
        authManager = aManager;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        AppUser createdUser = userServ.createUser(request);
        URI location = URI.create("/users/" + createdUser.getId());

        return ResponseEntity.created(location).body(UserResponse.from(createdUser));
    }

    @PostMapping("/login")
    public String login(@Valid @RequestBody LoginRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        return "Login successful";
    }
}
