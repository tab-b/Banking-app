package com.app.banking.controllers;
import com.app.banking.dto.CreateUserRequest;
import com.app.banking.dto.UserResponse;
import com.app.banking.model.AppUser;
import com.app.banking.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userServ;

    public UserController(UserService u) {
        userServ = u;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        AppUser createdUser = userServ.createUser(request);
        URI location = URI.create("/api/users" + createdUser.getId());

        return ResponseEntity.created(location).body(UserResponse.from(createdUser));
    }
}
