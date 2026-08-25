package com.app.banking.controllers;

import com.app.banking.config.SecurityConfig;
import com.app.banking.dto.CreateUserRequest;
import com.app.banking.dto.UserDTO;
import com.app.banking.model.AppUser;
import com.app.banking.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(AuthController.class)
@AutoConfigureRestTestClient
@Import(SecurityConfig.class)
public class AuthControllerTest {

    @MockitoBean
    private UserService userServ;
    @MockitoBean
    private AuthenticationManager aManager;
    @MockitoBean
    private SecurityContextRepository securityRepo;

    @Autowired
    private RestTestClient restTestClient;

    @Test
    public void testRegister() throws Exception{
        AppUser user = new AppUser("john", "doe", "test@example.com", Set.of("ROLE_USER"), "hashedPass", new ArrayList<>());
        when(userServ.createUser(any(CreateUserRequest.class))).thenReturn(user);
        var result = restTestClient.post()
                .uri("/auth/register")
                .body(new CreateUserRequest(
                        "john",
                        "doe",
                        "test@example.com",
                        "password123"
                ))
                .exchange()
                .returnResult(UserDTO.class);
        System.out.println(result.getResponseBody());

    }



}
