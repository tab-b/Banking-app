package com.app.banking.controllers;

import com.app.banking.dto.CreateUserRequest;
import com.app.banking.dto.LoginRequest;
import com.app.banking.dto.UserDTO;
import com.app.banking.model.AppUser;
import com.app.banking.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userServ;
    private final AuthenticationManager authManager;
    private final SecurityContextRepository securityContextRepository;

    public AuthController(UserService u, AuthenticationManager aManager, SecurityContextRepository securityContextRepository) {
        userServ = u;
        authManager = aManager;
        this.securityContextRepository = securityContextRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserRequest request) {
        AppUser createdUser = userServ.createUser(request);
        URI location = URI.create("/users/" + createdUser.getId());

        return ResponseEntity.created(location).body(UserDTO.from(createdUser));
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Authentication authentication =
                authManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.email(),
                                request.password()
                        )
                );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(
                context,
                httpRequest,
                httpResponse
        );
        return ResponseEntity.ok().body("Login success!");
    }


//    @PostMapping("/logout")
//    public ResponseEntity<Void> logout(HttpServletRequest httpRequest) {
//        SecurityContextHolder.clearContext();
//
//        HttpSession session = httpRequest.getSession(false);
//        if(session != null) {
//            session.invalidate();
//        }
//        return ResponseEntity.ok().build();
//    }
}
