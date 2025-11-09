package com.example.musicapp.dto;

import com.example.musicapp.entity.RoleName;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserDTO {
    private Long id;
    private String username;
    private boolean enabled; // Trạng thái (kích hoạt/vô hiệu hóa)
    private Set<RoleName> roles; // Danh sách vai trò
}
