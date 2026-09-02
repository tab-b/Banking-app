package com.app.banking.dto;

import java.util.List;

public record UserDashboardView(
    List<AccountDTO> accounts,
    String firstName,
    String lastName,
    List<TransactionDTO> recentTransactions
) {
}
