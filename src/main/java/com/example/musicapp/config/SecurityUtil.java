package com.example.musicapp.config;

import com.example.musicapp.entity.Account;
import com.example.musicapp.exception.ResourceNotFoundException;
import com.example.musicapp.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    // Phải dùng static repository theo cách này để inject vào static context
    private static AccountRepository accountRepository;

    @Autowired
    public SecurityUtil(AccountRepository accountRepository) {
        SecurityUtil.accountRepository = accountRepository;
    }

    /**
     * Lấy Account entity của người dùng đang đăng nhập
     */
    public static Account getCurrentUserAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null; // Không có ai đăng nhập
        }

        Object principal = authentication.getPrincipal();
        String email;

        if (principal instanceof User) {
            email = ((User) principal).getUsername();
        } else if (principal instanceof String) {
            email = (String) principal;
        } else {
            return null; // Principal không xác định
        }

        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản với email: " + email));
    }
}
