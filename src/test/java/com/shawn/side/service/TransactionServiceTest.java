package com.shawn.side.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.shawn.side.dto.response.TransactionResponse;
import com.shawn.side.entity.Account;
import com.shawn.side.entity.Transaction;
import com.shawn.side.entity.Transaction.TransactionType;
import com.shawn.side.exception.ResourceNotFoundException;
import com.shawn.side.repository.AccountRepository;
import com.shawn.side.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Account testAccount;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
         testAccount = Account.builder()
                .id(1L)
                .accountNumber("ACC-12345678")
                .build();
        testTransaction = Transaction.builder()
            .id(1L)
            .transactionType(TransactionType.DEPOSIT)
            .amount(BigDecimal.valueOf(500))
            .toAccount(testAccount)
            .description("測試存款")
            .build();

    }

    @Nested
    @DisplayName("getTransactionsByAccountId 分頁查詢測試")
    class GetTransactionByAccountIdTests {

        @Test
        @DisplayName("查詢成功 - 回傳分頁結果")
        void getTransactions_ShouldReturnPage_WhenAccountExists() {

            Pageable pageable = PageRequest.of(0, 10);

            Page<Transaction> fakePage = new PageImpl<>(
                List.of(testTransaction),
                pageable,
                1
            );

            when(accountRepository.existsById(testAccount.getId())).thenReturn(true);
            when(transactionRepository.findByAccountId(testAccount.getId(), pageable)).thenReturn(fakePage);

            Page<TransactionResponse> response = transactionService.getTransactionsByAccountId(testAccount.getId(), pageable);

            assertThat(response).isNotNull();
            assertThat(response.getTotalElements()).isEqualTo(1);
            assertThat(response.getTotalPages()).isEqualTo(1);
            assertThat(response.getNumber()).isEqualTo(0);
            assertThat(response.getSize()).isEqualTo(10);

            assertThat(response.getContent()).hasSize(1);
            assertThat(response.getContent().get(0).getTransactionType()).isEqualTo(testTransaction.getTransactionType());
            assertThat(response.getContent().get(0).getAmount()).isEqualTo(testTransaction.getAmount());

        }

        @Test
        @DisplayName("查詢成功 - 沒有交易紀錄 - 回傳空分頁")
        void getTransactions_ShouldReturnEmptyPage_WhenNoTransactions() {
            Pageable pageable = PageRequest.of(0, 10);
            
            Page<Transaction> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            when(accountRepository.existsById(testAccount.getId())).thenReturn(true);
            when(transactionRepository.findByAccountId(testAccount.getId(), pageable)).thenReturn(emptyPage);

            Page<TransactionResponse> response = transactionService.getTransactionsByAccountId(testAccount.getId(), pageable);

            assertThat(response).isNotNull();
            assertThat(response.getTotalElements()).isEqualTo(0);
            assertThat(response.getContent()).isEmpty();

        }

        @Test
        @DisplayName("查詢失敗 - 帳戶不存在")
        void getTransactions_ShouldThrowException_WhenAccountNotExists() {
            Pageable pageable = PageRequest.of(0, 10);

            when(accountRepository.existsById(testAccount.getId())).thenReturn(false);

            assertThatThrownBy(() -> transactionService.getTransactionsByAccountId(testAccount.getId(), pageable))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("帳戶不存在");

            verify(transactionRepository, never()).findByAccountId(any(), any());
        }
    }

}
