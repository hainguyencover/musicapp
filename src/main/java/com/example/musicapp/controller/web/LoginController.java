package com.example.musicapp.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/")
    public String showLoginPage() {
        // "login" là tên của file login.html (không cần .html vì đã cấu hình suffix)
        return "login";
    }
}
