package com.shawn.side.controller;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shawn.side.dto.request.LoginRequest;
import com.shawn.side.dto.request.RegisterRequest;
import com.shawn.side.dto.response.ApiResponse;
import com.shawn.side.dto.response.LoginResponse;
import com.shawn.side.dto.response.RegisterResponse;
import com.shawn.side.service.AuthService;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
        @Valid @RequestBody RegisterRequest request) {

            RegisterResponse response = authService.register(request);

            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("註冊成功", response));
        }
    
        @PostMapping("/login")
        public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request
        ) {
            LoginResponse response = authService.login(request);

            return ResponseEntity.ok(ApiResponse.ok("登入成功", response));
        }
        
}
