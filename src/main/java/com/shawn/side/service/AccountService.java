package com.shawn.side.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shawn.side.dto.request.CreateAccountRequest;
import com.shawn.side.dto.response.AccountResponse;
import com.shawn.side.entity.Account;
import com.shawn.side.entity.User;
import com.shawn.side.entity.Account.AccountStatus;
import com.shawn.side.exception.ResourceNotFoundException;
import com.shawn.side.repository.AccountRepository;
import com.shawn.side.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Transactional
    public AccountResponse createAccount(Long userId, CreateAccountRequest request){
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("用戶不存在"));
        
        String accountNumber = generateAccountNumber();

        Account account = Account.builder()
            .accountNumber(accountNumber)
            .user(user)
            .accountType(request.getAccountType())
            .build();
        
        Account savedAccount = accountRepository.save(account);
        return toAccountResponse(savedAccount);

    }

    @Transactional(readOnly = true)
    public AccountResponse getAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("帳戶不存在"));
        
        return toAccountResponse(account);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("用戶不存在");
        }

        List<Account> accounts = accountRepository.findByUserId(userId);

        return accounts.stream()
            .map(this::toAccountResponse)
            .collect(Collectors.toList());

    }

    @Transactional
    public void closeAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("帳戶不存在"));

        account.setStatus(AccountStatus.CLOSED);
        accountRepository.save(account);
    }




    private String generateAccountNumber() {
        return "ACC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private AccountResponse toAccountResponse(Account account) {
        return AccountResponse.builder()
            .id(account.getId())
            .accountNumber(account.getAccountNumber())
            .balance(account.getBalance())
            .accountType(account.getAccountType())
            .status(account.getStatus())
            .createdAt(account.getCreatedAt())
            .build();
    }

}
