package com.app.banking.services;

import com.app.banking.dto.TransactionDTO;
import com.app.banking.model.TransactionType;
import com.app.banking.queries.GetRecentTransactionsQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TransactionQueryHandler {
    private final JdbcTemplate dbConnection;

    public TransactionQueryHandler(JdbcTemplate dbConnection) {
        this.dbConnection = dbConnection;
    }

    public List<TransactionDTO> getTransactions(GetRecentTransactionsQuery query) {
        // Fetch transactions
        String transactionsSql = """
                SELECT
                    t.amount,
                    t.transaction_id,
                    t.type,
                    t.to_account_num,
                    u.f_name AS receiver_first_name,
                    u.last_name AS receiver_last_name
                FROM transaction t
                LEFT JOIN account a ON t.to_account_num = a.account_number
                LEFT JOIN users u ON a.owner_id = u.owner_id
                WHERE t.from_account = ?
                ORDER BY t.timestamp DESC
                LIMIT ?
                """;
        return dbConnection.query(transactionsSql, (rs, rowNum) ->
                new TransactionDTO(
                        rs.getBigDecimal("amount"),
                        UUID.fromString(rs.getString("transaction_id")),
                        TransactionType.valueOf(rs.getString("type")),
                        rs.getString("receiver_first_name"),
                        rs.getString("receiver_last_name"),
                        rs.getTimestamp("timestamp").toInstant()

                ), query.accountNum(), query.numberOfTransactions());
    }
}
