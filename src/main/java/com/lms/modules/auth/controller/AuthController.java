package com.lms.modules.auth.controller;

import com.lms.common.response.ApiResponse;
import com.lms.modules.auth.dto.AuthResponse;
import com.lms.modules.auth.dto.LoginRequest;
import com.lms.modules.auth.dto.RegisterRequest;
import com.lms.modules.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse result = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Account registered successfully!", result));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse result = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful!", result));
    }
}