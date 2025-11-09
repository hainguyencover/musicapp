package com.example.musicapp.service;

import com.example.musicapp.config.SecurityUtil;
import com.example.musicapp.dto.ProfileDTO;
import com.example.musicapp.entity.Account;
import org.springframework.stereotype.Service;


@Service
public class AccountService {

    // Không cần AccountRepository ở đây vì SecurityUtil đã giữ nó

    public ProfileDTO getCurrentUserProfile() {
        Account account = SecurityUtil.getCurrentUserAccount();
        if (account == null) {
            // Lỗi này không nên xảy ra nếu filter hoạt động đúng
            throw new RuntimeException("Không tìm thấy thông tin xác thực");
        }
        return mapToProfileDTO(account);
    }

    private ProfileDTO mapToProfileDTO(Account account) {
        ProfileDTO dto = new ProfileDTO();
        dto.setId(account.getId());
        dto.setEmail(account.getEmail());
        dto.setDisplayName(account.getDisplayName());
        dto.setRole(account.getRole());
        dto.setCreatedAt(account.getCreatedAt());
        return dto;
    }
}
