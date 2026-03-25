package com.shawn.side.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shawn.side.dto.request.DepositRequest;
import com.shawn.side.dto.request.TransferRequest;
import com.shawn.side.dto.request.WithdrawRequest;
import com.shawn.side.dto.response.ApiResponse;
import com.shawn.side.dto.response.TransactionResponse;
import com.shawn.side.service.TransactionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<TransactionResponse>> deposit(
        @Valid @RequestBody DepositRequest request
    ) {
        TransactionResponse response = transactionService.deposit(request);
        return ResponseEntity.ok(ApiResponse.ok("存款成功", response));
    }
    
    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<TransactionResponse>> withdraw(
        @Valid @RequestBody WithdrawRequest request
    ) {
        TransactionResponse response = transactionService.withdraw(request);
        return ResponseEntity.ok(ApiResponse.ok("提款成功", response));

    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(
        @Valid @RequestBody TransferRequest request
    ) {

        TransactionResponse response = transactionService.transfer(request);
        return ResponseEntity.ok(ApiResponse.ok("轉帳成功", response));
    }


    @GetMapping
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getTransactions(
        @RequestParam Long accountId,
        Pageable pageable
    ) {
        Page<TransactionResponse> response = transactionService.getTransactionsByAccountId(accountId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    } 


}
