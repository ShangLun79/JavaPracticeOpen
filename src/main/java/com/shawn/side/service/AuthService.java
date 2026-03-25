package com.shawn.side.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shawn.side.dto.request.LoginRequest;
import com.shawn.side.dto.request.RegisterRequest;
import com.shawn.side.dto.response.LoginResponse;
import com.shawn.side.dto.response.RegisterResponse;
import com.shawn.side.entity.User;
import com.shawn.side.exception.BusinessException;
import com.shawn.side.repository.UserRepository;
import com.shawn.side.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        
        if (userRepository.existsByEmail(request.getEmail())){
            throw new BusinessException("該Email已被註冊");
        }

        User user = User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .name(request.getName())
            .build();
        
        User savedUser = userRepository.save(user);

        return RegisterResponse.builder()
            .id(savedUser.getId())
            .email(savedUser.getEmail())
            .name(savedUser.getName())
            .build();
    }

    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );
        } catch(BadCredentialsException e) {
            throw new BusinessException("帳號密碼錯誤");
        }

        String token = jwtTokenProvider.generateToken(request.getEmail());

        return LoginResponse.builder()
            .token(token)
            .email(request.getEmail())
            .build();

        
    }
}
