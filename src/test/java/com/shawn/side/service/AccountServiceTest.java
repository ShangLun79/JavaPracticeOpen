package com.shawn.side.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shawn.side.dto.request.CreateAccountRequest;
import com.shawn.side.dto.response.AccountResponse;
import com.shawn.side.entity.Account;
import com.shawn.side.entity.User;
import com.shawn.side.entity.Account.AccountStatus;
import com.shawn.side.entity.Account.AccountType;
import com.shawn.side.exception.ResourceNotFoundException;
import com.shawn.side.repository.AccountRepository;
import com.shawn.side.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountService accountService;

    private User testUser;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .name("test user")
                .build();

        testAccount = Account.builder()
                .id(1L)
                .accountNumber("ACC-12345678")
                .user(testUser)
                .balance(BigDecimal.ZERO)
                .accountType(AccountType.SAVINGS)
                .status(AccountStatus.ACTIVE)
                .build();

    }

    @Nested
    @DisplayName("createAccount 方法測試")
    class CreateAccountTests {

        @Test
        @DisplayName("開戶成功")
        void createAccount_shouldReturnResponse_WhenUserExists() {
            CreateAccountRequest request = CreateAccountRequest.builder()
                    .accountType(AccountType.SAVINGS)
                    .build();

            when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
            when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

            AccountResponse response = accountService.createAccount(testUser.getId(), request);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(testAccount.getId());
            assertThat(response.getAccountNumber()).isEqualTo(testAccount.getAccountNumber());
            assertThat(response.getAccountType()).isEqualTo(testAccount.getAccountType());
            assertThat(response.getStatus()).isEqualTo(testAccount.getStatus());

            assertThat(response.getBalance()).isEqualByComparingTo(testAccount.getBalance());
            verify(accountRepository, times(1)).save(any(Account.class));
        }

        @Test
        @DisplayName("開戶失敗-客戶不存在")
        void createAccount_ShouldThrowException_WhenUserNotFound() {
            CreateAccountRequest request = CreateAccountRequest.builder()
                    .accountType(AccountType.SAVINGS)
                    .build();
            long testUserId = 99L;
            when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> accountService.createAccount(testUserId, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("用戶不存在");

            verify(accountRepository, never()).save(any(Account.class));

        }

    }

    @Nested
    @DisplayName("getAccount 方法測試")
    class GetAccountTests {

        @Test
        @DisplayName("查詢成功 - 帳戶存在")
        void getAccount_ShouldReturnResponse_WhenAccountExists() {
            when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.of(testAccount));

            AccountResponse response = accountService.getAccount(testAccount.getId());

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(testAccount.getId());
            assertThat(response.getAccountNumber()).isEqualTo(testAccount.getAccountNumber());

        }

        @Test
        @DisplayName("查詢失敗 - 帳戶不存在")
        void getAccount_ShouldThrowException_WhenAccountNotFound() {
            long testAccountId = 99L;
            when(accountRepository.findById(testAccountId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> accountService.getAccount(testAccountId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("帳戶不存在");

        }
    }

    @Nested
    @DisplayName("getAccountsByUserId 方法測試")
    class GetAccountsByUserIdTests {

        @Test
        @DisplayName("查詢成功 - 回傳帳戶列表")
        void getAccountsByUserId_ShouldReturnList_WhenUserExists() {
            Account secondAccount = Account.builder()
                    .id(2L)
                    .accountNumber("ACC-87654321")
                    .user(testUser)
                    .balance(BigDecimal.valueOf(1000))
                    .accountType(AccountType.CHECKING)
                    .status(AccountStatus.ACTIVE)
                    .build();

            when(userRepository.existsById(testUser.getId())).thenReturn(true);
            when(accountRepository.findByUserId(testUser.getId())).thenReturn(List.of(testAccount, secondAccount));

            List<AccountResponse> responses = accountService.getAccountsByUserId(testUser.getId());

            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).getAccountNumber()).isEqualTo(testAccount.getAccountNumber());
            assertThat(responses.get(1).getAccountNumber()).isEqualTo(secondAccount.getAccountNumber());

        }

        @Test
        @DisplayName("查詢成功 - 用戶沒有帳戶，回傳空陣列")
        void getAccountsByUserId_ShouldReturnEmptyList_WhenNoAccounts() {

            when(userRepository.existsById(testUser.getId())).thenReturn(true);
            when(accountRepository.findByUserId(testUser.getId())).thenReturn(List.of());

            List<AccountResponse> responses = accountService.getAccountsByUserId(testUser.getId());

            assertThat(responses).isEmpty();

        }

        @Test
        @DisplayName("查詢失敗 - 用戶不存在")
        void getAccountsByUserId_ShouldThrowException_WhenUserNotExists() {
            long testUserId = 99L;

            when(userRepository.existsById(testUserId)).thenReturn(false);

            assertThatThrownBy(() -> accountService.getAccountsByUserId(testUserId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("用戶不存在");

            verify(accountRepository, never()).findByUserId(any());

        }

    }

    @Nested
    @DisplayName("closeAccount 方法測試")
    class CloseAccountTests {

        @Test
        @DisplayName("銷戶成功 - 帳戶狀態改為CLOSED")
        void closeAccount_ShouldSetStatusClosed_WhenAccountExists() {

            when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.of(testAccount));

            accountService.closeAccount(testAccount.getId());

            assertThat(testAccount.getStatus()).isEqualTo(AccountStatus.CLOSED);

            verify(accountRepository, times(1)).save(testAccount);
        }

        @Test
        @DisplayName("銷戶失敗 - 帳戶不存在")
        void closeAccount_ShouldThrowException_WhenAccountNotFound() {
            long testAccountId = 99L;
            when(accountRepository.findById(testAccountId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> accountService.closeAccount(testAccountId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("帳戶不存在");

            verify(accountRepository, never()).save(any(Account.class));
        }
    }

}
