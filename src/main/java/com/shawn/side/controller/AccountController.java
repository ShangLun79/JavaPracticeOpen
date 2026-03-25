package com.shawn.side.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shawn.side.dto.request.CreateAccountRequest;
import com.shawn.side.dto.response.AccountResponse;
import com.shawn.side.dto.response.ApiResponse;
import com.shawn.side.service.AccountService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(
        @RequestParam Long userId,
        @Valid @RequestBody CreateAccountRequest request
    ){
        AccountResponse response = accountService.createAccount(userId, request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.ok("開戶成功", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccount(
        @PathVariable Long id
    ){
        AccountResponse response = accountService.getAccount(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getMyAccounts(
        @RequestParam Long userId
    ) {
        List<AccountResponse> responses = accountService.getAccountsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.ok(responses));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> closeAccount(
        @PathVariable Long id
    ) {
        accountService.closeAccount(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
