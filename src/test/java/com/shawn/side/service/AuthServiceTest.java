package com.shawn.side.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.shawn.side.dto.request.LoginRequest;
import com.shawn.side.dto.request.RegisterRequest;
import com.shawn.side.dto.response.LoginResponse;
import com.shawn.side.dto.response.RegisterResponse;
import com.shawn.side.entity.User;
import com.shawn.side.exception.BusinessException;
import com.shawn.side.repository.UserRepository;
import com.shawn.side.security.JwtTokenProvider;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    @Nested
    @DisplayName("register 方法測試")
    class RegisterTests {

        @Test
        @DisplayName("註冊成功 - Email 未被使用")
        void register_ShouldReturnResponse_WhenEmailNotExists() {

            // arrange

            String testEmail = "test@example.com";
            String testPassword = "whatever";
            String testName = "Test User";
            RegisterRequest request = RegisterRequest.builder()
                .email(testEmail)
                .password(testPassword)
                .name(testName)
                .build();

            String testEncodePassword = "encodePassword";
            User savedUser = User.builder()
                .id(1L)
                .email(testEmail)
                .password(testEncodePassword)
                .name(testName)
                .build();
            
            when(userRepository.existsByEmail(testEmail)).thenReturn(false);
            when(passwordEncoder.encode(testPassword)).thenReturn(testEncodePassword);
            when(userRepository.save(any(User.class))).thenReturn(savedUser);


            // act 
            RegisterResponse response = authService.register(request);

            // assert
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(savedUser.getId());
            assertThat(response.getEmail()).isEqualTo(savedUser.getEmail());
            assertThat(response.getName()).isEqualTo(savedUser.getName());

            verify(userRepository, times(1)).save(any(User.class));

            
        }

        @Test
        @DisplayName("註冊失敗 - Email已被使用")
        void register_shouldThrowException_WhenEmailExists() {
            String testEmail = "test@example.com";
            String testPassword = "password123";
            String testUser = "Test User";

            // arrange
            RegisterRequest request = RegisterRequest.builder()
                .email(testEmail)
                .password(testPassword)
                .name(testUser)
                .build();

            when(userRepository.existsByEmail(testEmail)).thenReturn(true);

            // assert / act
            assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("該Email已被註冊");
            

            verify(userRepository, never()).save(any(User.class));

        }
        
    }

    @Nested
    @DisplayName("login 方法測試")
    class LoginTests {
        
        @Test
        @DisplayName("登入成功 - 帳號密碼正確")
        void login_ShouldReturnToken_WhenCredientialsValid() {
            
            String testEmail = "test@example.com";
            String testPassword = "password123";
            LoginRequest request = LoginRequest.builder()
                .email(testEmail)
                .password(testPassword)
                .build();
            
            String testToken = "mock-jwt-token";
            when(jwtTokenProvider.generateToken(testEmail))
                .thenReturn(testToken);
            
            LoginResponse response = authService.login(request);

            assertThat(response).isNotNull();
            assertThat(response.getToken()).isEqualTo(testToken);
            assertThat(response.getEmail()).isEqualTo(testEmail);

            verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
            
        }

        @Test
        @DisplayName("登入失敗 - 帳號密碼錯誤")
        void login_ShouldThrowException_WhenCredentialsInvalid()
        {
            String testEmail = "test@example.com";
            String testPassword = "wrongPassword";
            LoginRequest request = LoginRequest.builder()
                .email(testEmail)
                .password(testPassword)
                .build();
            

            when(
                authenticationManager.authenticate(
                    any(UsernamePasswordAuthenticationToken.class)
                )
            ).thenThrow(new BadCredentialsException("Bad credentials"));

            assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("帳號密碼錯誤");
            
            verify(jwtTokenProvider, never()).generateToken(anyString());

        }
    }

}
