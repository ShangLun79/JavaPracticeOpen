package com.shawn.side.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shawn.side.dto.response.AccountResponse;
import com.shawn.side.dto.response.ApiResponse;
import com.shawn.side.entity.Account;
import com.shawn.side.repository.JdbcAccountDao;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping("/api/v1/jdbc")
@RequiredArgsConstructor
public class JdbcPracticeController {

    private final JdbcAccountDao jdbcAccountDao;

    @GetMapping("/accounts/{id}")
    public ResponseEntity<ApiResponse<AccountResponse>> findById(@PathVariable Long id) {
        return jdbcAccountDao.findById(id)
            .map(account -> ResponseEntity.ok(ApiResponse.ok(toAccountResponse(account))))
            .orElse(ResponseEntity.notFound().build());

    }

    @GetMapping("/accounts")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> findByUserId(@RequestParam Long userId) {
        List<AccountResponse> accounts = jdbcAccountDao.findByUserId(userId)
            .stream()
            .map(account -> toAccountResponse(account))
            .toList();
        return ResponseEntity.ok(ApiResponse.ok(accounts));
    }

    @GetMapping("/accounts/{id}/balance")
    public ResponseEntity<ApiResponse<BigDecimal>> findBalance(@PathVariable Long id) {
        return jdbcAccountDao.findBalanceById(id)
            .map(balance -> ResponseEntity.ok(ApiResponse.ok(balance)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/accounts/{id}/balance")
    public ResponseEntity<ApiResponse<String>> updateBalance(@PathVariable Long id, @RequestParam BigDecimal balance) {
        int affected = jdbcAccountDao.updateBalance(id, balance);

        if (affected > 0) {
            return ResponseEntity.ok(ApiResponse.ok("餘額更新成功", "updated: " + affected + " row(s)"));
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("找不到指定帳戶"));
        }
    }

    @GetMapping("/accounts/count")
    public ResponseEntity<ApiResponse<Integer>> countByUserId(@RequestParam Long userId) {
        int count = jdbcAccountDao.countByUserId(userId);

        return ResponseEntity.ok(ApiResponse.ok(count));
    }
    
    

    private AccountResponse toAccountResponse(Account account) {
        return AccountResponse.builder()
            .id(account.getId())
            .balance(account.getBalance())
            .accountNumber(account.getAccountNumber())
            .accountType(account.getAccountType())
            .createdAt(account.getCreatedAt())
            .status(account.getStatus())
            .build();
            
    }
}
