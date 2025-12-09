package com.workhive.userservice.controller;

import com.workhive.userservice.dto.AuthResponse;
import com.workhive.userservice.domain.dto.LoginRequest;
import com.workhive.userservice.domain.dto.RegisterRequest;
import com.workhive.userservice.domain.dto.UserResponse;
import com.workhive.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse user = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = userService.login(request);
        AuthResponse response = AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .message("Login successful")
                .build();
        return ResponseEntity.ok(response);
    }
}

