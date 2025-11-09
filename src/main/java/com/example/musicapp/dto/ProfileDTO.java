package com.example.musicapp.dto;

import com.example.musicapp.entity.Role;
import lombok.Data;

import java.time.LocalDateTime;

// DTO này chỉ trả về thông tin an toàn của người dùng
@Data
public class ProfileDTO {
    private Long id;
    private String email;
    private String displayName;
    private Role role;
    private LocalDateTime createdAt;
}
