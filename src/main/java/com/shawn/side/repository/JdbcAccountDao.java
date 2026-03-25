package com.shawn.side.repository;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.shawn.side.entity.Account;
import com.shawn.side.entity.Account.AccountStatus;
import com.shawn.side.entity.Account.AccountType;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JdbcAccountDao {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Account> accountRowMapper = (rs, rowNum) -> {
        
        Account account = Account.builder()
            .id(rs.getLong("id"))
            .accountNumber(rs.getString("account_number"))
            .balance(rs.getBigDecimal("balance"))
            .accountType(AccountType.valueOf(rs.getString("account_type")))
            .status(AccountStatus.valueOf(rs.getString("status")))
            .build();
        return account;
    };

    public Optional<Account> findById(Long id) {
        String sql = "SELECT * FROM accounts WHERE id = ?";
        try {
            Account account = jdbcTemplate.queryForObject(sql, accountRowMapper, id);
            return Optional.ofNullable(account);
        }
        catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }

    }

    public List<Account> findByUserId(Long userId){
        String sql = "SELECT * FROM accounts WHERE user_id = ?";
        return jdbcTemplate.query(sql, accountRowMapper, userId);
    }

    public Optional<BigDecimal> findBalanceById(Long accountId) {
        String sql = "SELECT balance FROM accounts WHERE id = ?";
        try {
            BigDecimal balance = jdbcTemplate.queryForObject(sql, BigDecimal.class, accountId);
            return Optional.ofNullable(balance);
        }
        catch(EmptyResultDataAccessException e) {
            return Optional.empty();
        }

    }

    public int updateBalance(Long accountId, BigDecimal newBalance) {
        String sql = "UPDATE accounts SET balance = ?, updated_at = NOW() WHERE id = ?";

        return jdbcTemplate.update(sql, newBalance, accountId);
    }

    public int countByUserId(Long userId) {
        String sql = "SELECT COUNT(*) FROM accounts WHERE user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);

        return count != null ? count : 0;

    }





}
