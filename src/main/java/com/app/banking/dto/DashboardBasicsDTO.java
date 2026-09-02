package com.app.banking.dto;

import com.app.banking.model.Account;

import java.util.List;

public record DashboardBasicsDTO(
        String firstName,
        String lastName,
        List<AccountDTO> accounts
) {
}
