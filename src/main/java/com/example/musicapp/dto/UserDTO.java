package com.example.musicapp.dto;

import com.example.musicapp.entity.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserDTO {
    private Long id;
    private String username;
    private boolean enabled; // Trạng thái (kích hoạt/vô hiệu hóa)
    private Set<Role> roles; // Danh sách vai trò
}
