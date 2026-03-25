package com.shawn.side.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shawn.side.dto.request.DepositRequest;
import com.shawn.side.dto.request.TransferRequest;
import com.shawn.side.dto.request.WithdrawRequest;
import com.shawn.side.dto.response.TransactionResponse;
import com.shawn.side.entity.Account;
import com.shawn.side.entity.Transaction;
import com.shawn.side.entity.Transaction.TransactionType;
import com.shawn.side.exception.InsufficientBalanceException;
import com.shawn.side.exception.ResourceNotFoundException;
import com.shawn.side.repository.AccountRepository;
import com.shawn.side.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public TransactionResponse deposit(DepositRequest request) {
        Account account = accountRepository.findById(request.getAccountId())
            .orElseThrow(() -> new ResourceNotFoundException("帳戶不存在"));

        account.setBalance(account.getBalance().add(request.getAmount()));
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
            .transactionType(TransactionType.DEPOSIT)
            .amount(request.getAmount())
            .toAccount(account)
            .description(request.getDescription())
            .build();
        
        Transaction saved = transactionRepository.save(transaction);
        return toTransactionResponse(saved);
        
    }

    @Transactional
    public TransactionResponse withdraw(WithdrawRequest request) {
        Account account = accountRepository.findById(request.getAccountId())
            .orElseThrow(() -> new ResourceNotFoundException("帳戶不存在"));
        
        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("餘額不足");
        }
        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
            .amount(request.getAmount())
            .transactionType(TransactionType.WITHDRAW)
            .description(request.getDescription())
            .fromAccount(account)
            .build();
        
        Transaction saved = transactionRepository.save(transaction);

        return toTransactionResponse(saved);

    }

    @Transactional
    public TransactionResponse transfer(TransferRequest request) {
        Account fromAccount = accountRepository.findById(request.getFromAccountId())
            .orElseThrow(() -> new ResourceNotFoundException("來源帳戶不存在"));
        
        Account toAccount = accountRepository.findById(request.getToAccountId())
            .orElseThrow(() -> new ResourceNotFoundException("目標帳戶不存在"));

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("餘額不足");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction transaction = Transaction.builder()
            .fromAccount(fromAccount)
            .toAccount(toAccount)
            .amount(request.getAmount())
            .transactionType(TransactionType.TRANSFER)
            .description(request.getDescription())
            .build();
        
        Transaction saved = transactionRepository.save(transaction);
        return toTransactionResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getTransactionsByAccountId(Long accountId, Pageable pageable) {
        if (!accountRepository.existsById(accountId)) {
            throw new ResourceNotFoundException("帳戶不存在");
        }

        Page<Transaction> transactions = transactionRepository.findByAccountId(accountId, pageable);
        
        return transactions.map(this::toTransactionResponse);
    }



    private TransactionResponse toTransactionResponse(Transaction transaction) {
        TransactionResponse response = TransactionResponse.builder()
            .id(transaction.getId())
            .transactionType(transaction.getTransactionType())
            .fromAccountNumber(
                transaction.getFromAccount() != null
                    ? transaction.getFromAccount().getAccountNumber()
                    : null
            )
            .amount(transaction.getAmount())
            .toAccountNumber(
                transaction.getToAccount() != null 
                    ? transaction.getToAccount().getAccountNumber()
                    : null
            )
            .description(transaction.getDescription())
            .createdAt(transaction.getCreatedAt())
            .build();
            return response;

    }

}
