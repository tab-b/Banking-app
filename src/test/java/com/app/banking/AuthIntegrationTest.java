package com.app.banking;

import com.app.banking.dto.CreateUserRequest;
import com.app.banking.dto.LoginRequest;
import com.app.banking.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
public class AuthIntegrationTest {
    @Autowired
    private RestTestClient client;

    @Test
    void LoginRegisterMeLogoutFlow() {
        // REGISTER
        var registerResult = client.post()
                .uri("/auth/register")
                .body(new CreateUserRequest(
                        "john",
                        "doe",
                        "test@example.com",
                        "password123"
                ))
                .exchange()
                .returnResult(UserDTO.class);

        assertEquals(HttpStatus.CREATED, registerResult.getStatus());
        UserDTO registerResponse = registerResult.getResponseBody();
        System.out.println(registerResponse);
        assertEquals("test@example.com", registerResponse.email());
        assertNotNull(registerResponse.id());
        //

        // LOGIN

        var loginResult = client.post()
                .uri("auth/login")
                .body(new LoginRequest(
                        "test@example.com",
                        "password123"
                ))
                .exchange()
                .returnResult(String.class);
        assertEquals(HttpStatus.OK, loginResult.getStatus());
        System.out.println(loginResult.getResponseBody());
        var sessionCookie = loginResult.getResponseCookies().getFirst("JSESSIONID");
        assertNotNull(sessionCookie);

        //

        // GET CURRENT USER
        var meResult = client.get()
                .uri("/users/me")
                .cookie("JSESSIONID", sessionCookie.getValue())
                .exchange()
                .returnResult(UserDTO.class);
        assertEquals(HttpStatus.OK, meResult.getStatus());
        UserDTO currentUser = meResult.getResponseBody();
        assertNotNull(currentUser);
        System.out.println(currentUser);
        assertEquals("test@example.com", currentUser.email());
    }


}
