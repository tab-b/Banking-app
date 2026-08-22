package com.app.banking.controllers;

import com.app.banking.dto.UserDTO;
import com.app.banking.model.AppUser;
import com.app.banking.services.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userServ;
    private final SecurityContextRepository securityContextRepository;
    private final AuthenticationManager authManager;

    public UserController(UserService userServ, SecurityContextRepository securityContextRepository, AuthenticationManager authManager) {
        this.userServ = userServ;
        this.securityContextRepository = securityContextRepository;
        this.authManager = authManager;
    }

    @GetMapping("/me")
    public UserDTO getCurrentUserDetails() {
        SecurityContext currentContext = SecurityContextHolder.getContext();
        Authentication currentAuth = currentContext.getAuthentication();
        String email = currentAuth.getName();
        AppUser currentUser = userServ.getUserByEmail(email).orElseThrow(() -> new NoSuchElementException());
        return UserDTO.from(currentUser);
    }
}
