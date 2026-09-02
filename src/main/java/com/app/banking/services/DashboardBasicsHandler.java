package com.app.banking.services;

import com.app.banking.dto.AccountDTO;
import com.app.banking.dto.DashboardBasicsDTO;
import com.app.banking.dto.TransactionDTO;
import com.app.banking.dto.UserDashboardView;
import com.app.banking.model.AccountType;
import com.app.banking.model.Status;
import com.app.banking.model.TransactionType;
import com.app.banking.queries.GetUserDashboardQuery;
import jakarta.transaction.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class DashboardBasicsHandler {
    private final JdbcTemplate jdbcTemplate;

    public DashboardBasicsHandler(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public DashboardBasicsDTO handle(GetUserDashboardQuery query) {
        // Fetch accounts
        String accountsSql = """
            SELECT id, type, balance, account_number, status, opening_date, closing_date
            FROM account
            WHERE user_id = ?
            """;
        List<AccountDTO> accounts = jdbcTemplate.query(accountsSql, (rs, rowNum) ->
                new AccountDTO(
                        AccountType.valueOf(rs.getString("type")),
                        rs.getLong("id"),
                        rs.getString("account_number"),
                        Status.valueOf(rs.getString("status")),
                        rs.getObject("opening_date", LocalDate.class),
                        rs.getObject("closing_date", LocalDate.class),
                        rs.getBigDecimal("balance")

                ));
        // Fetch user info and put everything together
        String userInfoSql = "SELECT f_name, l_name FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(userInfoSql, (rs, rowNum) ->
                new DashboardBasicsDTO(
                        rs.getString("f_name"),
                        rs.getString("l_name"),
                        accounts
                ), query.userId());

    }

}
