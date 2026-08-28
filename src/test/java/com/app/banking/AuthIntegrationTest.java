package com.app.banking;

import com.app.banking.dto.CreateUserRequest;
import com.app.banking.dto.LoginRequest;
import com.app.banking.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
public class AuthIntegrationTest {
    @Autowired
    private RestTestClient client;

     private EntityExchangeResult<CsrfDTO> getCsrfToken() {
        var token = client.get()
                .uri("/csrf")
                .exchange()
                .returnResult(CsrfDTO.class);
//         System.out.println("CSRF token: " + token.getResponseBody().token());
//         System.out.println("CSRF header: " + token.getResponseBody().headerName());
//         System.out.println("CSRF cookies: " + token.getResponseCookies());

         return token;
    }

//    @Test
//    void test() {
//        var token = getCsrfToken();
//    }

    @Test
    void LoginRegisterMeLogoutFlow() {
         var csrfResult = getCsrfToken();
         CsrfDTO csrfBody = csrfResult.getResponseBody();
         String cookie = csrfResult.getResponseCookies().getFirst("XSRF-TOKEN").getValue();
        // REGISTER

        var registerResult = client.post()
                .uri("/auth/register")
                .cookie("XSRF-TOKEN", cookie)
                .header(csrfBody.headerName(), csrfBody.token())
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
                .cookie("XSRF-TOKEN", cookie)
                .header(csrfBody.headerName(), csrfBody.token())
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
                .cookie("XSRF-TOKEN", cookie)
                .header(csrfBody.headerName(), csrfBody.token())
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
