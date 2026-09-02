package com.app.banking.queries;

public record GetRecentTransactionsQuery(
        String accountNum,
        int numberOfTransactions
) {
}
