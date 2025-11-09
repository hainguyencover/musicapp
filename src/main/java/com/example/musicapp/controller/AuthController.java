package com.example.musicapp.controller;

import com.example.musicapp.dto.LoginRequest;
import com.example.musicapp.dto.LoginResponse;
import com.example.musicapp.dto.RegisterRequest;
import com.example.musicapp.entity.Account;
import com.example.musicapp.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        // AuthService sẽ ném RuntimeException nếu email tồn tại,
        // GlobalExceptionHandler sẽ bắt và trả về 400
        Account account = authService.register(registerRequest);

        // Trả về 201 Created cùng với thông tin (không bao gồm password)
        // Tạm thời trả về thông báo thành công
        return new ResponseEntity<>("Đăng ký tài khoản thành công!", HttpStatus.CREATED);
    }
}
