package com.app.banking;

import com.app.banking.dto.CreateUserRequest;
import com.app.banking.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
public class AuthIndividualTests {
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

    @Test
    void registerRequiresCsrf() {
        var result = client.post()
                .uri("auth/register")
                .body(new CreateUserRequest(
                        "john",
                        "doe",
                        "tester@gmail.com",
                        "pass456"
                ))
                .exchange()
                .returnResult(UserDTO.class);
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatus());
    }

    @Test
    void registerWithValidCsrfSucceeds() {
        var csrfResult = getCsrfToken();
        CsrfDTO csrfBody = csrfResult.getResponseBody();
        String cookie = csrfResult.getResponseCookies().getFirst("XSRF-TOKEN").getValue();

        var result = client.post()
                .uri("/auth/register")
                .cookie("XSRF-TOKEN", cookie)
                .header(csrfBody.headerName(), csrfBody.token())
                .body(new CreateUserRequest(
                        "teb",
                        "doe",
                        "testman@gmail.com",
                        "pass44456"
                ))
                .exchange()
                .returnResult(UserDTO.class);
        System.out.println("STATUS: " + result.getStatus());
        System.out.println("HEADERS: " + result.getResponseHeaders());
        System.out.println("BODY: " + result.getResponseBody());
        assertEquals(HttpStatus.CREATED, result.getStatus());
    }
}
