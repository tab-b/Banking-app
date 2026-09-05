package com.app.banking.authorization;


import com.app.banking.CsrfDTO;
import com.app.banking.dto.AccountDTO;
import com.app.banking.dto.CreateUserRequest;
import com.app.banking.dto.LoginRequest;
import com.app.banking.dto.UserDTO;
import com.app.banking.model.Account;
import com.app.banking.model.AccountType;
import com.app.banking.model.AppUser;
import com.app.banking.repositories.AccountRepository;
import com.app.banking.repositories.UserRepository;
import com.app.banking.services.AccountNumberService;
import com.app.banking.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
public class AccountAuthorizationTests {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountNumberService accountNumberService;

    @Autowired
    private RestTestClient client;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String cookie;
    private String cookieName = "XSRF-TOKEN";
    private CsrfDTO csrfBody;

    private String sessionCookie;
    private final String sessionCookieName = "SESSION";

    private AppUser user1;
    private AppUser user2;

    private Account account1;
    private Account account2;

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

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
        userRepository.deleteAll();
        user1 = userRepository.save(new AppUser(
                "john",
                "doe",
                "test@gmail.com",
                Set.of("ROLE_USER"),
                passwordEncoder.encode("password12345")
        ));

//
        user2 = userRepository.save(new AppUser(
                "bob",
                "joe",
                "bobjoe@gmail.com",
                Set.of("ROLE_USER"),
                passwordEncoder.encode("bobberjoe3456")
        ));

        account1 = accountRepository.save(new Account(
                AccountType.CHECKING,
                accountNumberService.generateUnique(),
                user1.getId()
        ));

        account2 = accountRepository.save(new Account(
                AccountType.CHECKING,
                accountNumberService.generateUnique(),
                user2.getId()
        ));

        var csrfResult = getCsrfToken();
        csrfBody = csrfResult.getResponseBody();
        cookie = csrfResult.getResponseCookies().getFirst(cookieName).getValue();


        // LOGIN
        var loginResult = client.post()
                .uri("/auth/login")
                .cookie("XSRF-TOKEN", cookie)
                .header(csrfBody.headerName(), csrfBody.token())
                .body(new LoginRequest(
                        "test@gmail.com",
                        "password12345"
                ))
                .exchange()
                .returnResult(String.class);
        System.out.println(loginResult.getResponseCookies());

        sessionCookie = loginResult.getResponseCookies()
                .getFirst(sessionCookieName)
                .getValue();

//        var loginResult2 = client.post()
//                .uri("auth/login")
//                .cookie("XSRF-TOKEN", cookie)
//                .header(csrfBody.headerName(), csrfBody.token())
//                .body(new LoginRequest(
//                        "test2@example.com",
//                        "password12345"
//                ))
//                .exchange()
//                .returnResult(String.class);

    }

    @Test
    void accessCurrentUserAccount() {
        var result = client.get()
                .uri("/api/accounts")
                .cookie(sessionCookieName, sessionCookie)
                .exchange()
                .returnResult(new ParameterizedTypeReference<List<AccountDTO>>() {
                });
        List<AccountDTO> accounts = result.getResponseBody();
        assertEquals(HttpStatus.OK, result.getStatus());
        assertNotNull(accounts);
        assertEquals(1, accounts.size());
        assertEquals(account1.getAccountNumber(), accounts.getFirst().accountNumber());
    }

    @Test
    void accessOtherAccountShouldThrow() {
        var result = client.get()
                .uri("/api/accounts/" + account2.getAccountNumber())
                .cookie(sessionCookieName, sessionCookie)
                .exchange()
                .returnResult(String.class);
        String body = result.getResponseBody();
        System.out.println(body);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatus());
    }

}
