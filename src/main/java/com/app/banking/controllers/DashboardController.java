package com.app.banking.controllers;

import com.app.banking.dto.DashboardBasicsDTO;
import com.app.banking.dto.TransactionDTO;
import com.app.banking.dto.UserDashboardView;
import com.app.banking.queries.GetRecentTransactionsQuery;
import com.app.banking.queries.GetUserDashboardQuery;
import com.app.banking.security.CustomUserDetails;
import com.app.banking.services.DashboardBasicsHandler;
import com.app.banking.services.TransactionQueryHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final TransactionQueryHandler transHandler;
    private final DashboardBasicsHandler dashBasicsHandler;

    public DashboardController(TransactionQueryHandler transHandler, DashboardBasicsHandler dbBasicsHandler) {
        this.transHandler = transHandler;
        this.dashBasicsHandler = dbBasicsHandler;
    }

    public ResponseEntity<UserDashboardView> getDashboard(@AuthenticationPrincipal CustomUserDetails currentUser, @RequestParam String primaryAccountNum) {
        CompletableFuture<DashboardBasicsDTO> dashBasics =
                CompletableFuture.supplyAsync(() -> dashBasicsHandler.handle(new GetUserDashboardQuery(currentUser.getId())));
        CompletableFuture<List<TransactionDTO>> recentTransactions =
                CompletableFuture.supplyAsync(() -> transHandler.getTransactions(new GetRecentTransactionsQuery(primaryAccountNum, 10)));
        CompletableFuture.allOf(dashBasics, recentTransactions).join();

        var finalView = new UserDashboardView(
                dashBasics.join().accounts(),
                dashBasics.join().firstName(),
                dashBasics.join().lastName(),
                recentTransactions.join()
        );
        return ResponseEntity.ok(finalView);
    }
}
