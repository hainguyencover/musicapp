package com.example.musicapp.controller;

import com.example.musicapp.dto.ProfileDTO;
import com.example.musicapp.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me") // API cho người dùng hiện tại
public class AccountController {

    @Autowired
    private AccountService accountService;

    // GET /api/me/profile
    @GetMapping("/profile")
    public ResponseEntity<ProfileDTO> getMyProfile() {
        return ResponseEntity.ok(accountService.getCurrentUserProfile());
    }
}
