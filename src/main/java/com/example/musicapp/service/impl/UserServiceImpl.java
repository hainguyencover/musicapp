package com.example.musicapp.service.impl;

import com.example.musicapp.dto.UserRegistrationDTO;
import com.example.musicapp.entity.RoleName;
import com.example.musicapp.entity.User;
import com.example.musicapp.exception.DuplicateNameException;
import com.example.musicapp.repository.UserRepository;
import com.example.musicapp.service.IUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void registerNewUser(UserRegistrationDTO dto) {
        // 1. Kiểm tra mật khẩu khớp
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            // Ném lỗi (Controller sẽ bắt)
            throw new IllegalArgumentException("Mật khẩu không khớp!");
        }

        // 2. Kiểm tra tên đăng nhập tồn tại
        if (userRepository.existsByUsernameIgnoreCase(dto.getUsername())) {
            // Ném lỗi (Controller sẽ bắt)
            throw new DuplicateNameException("Tên đăng nhập '" + dto.getUsername() + "' đã tồn Tồn tại.");
        }

        // 3. Tạo User mới
        User newUser = new User();
        newUser.setUsername(dto.getUsername());
        // 4. Mã hóa mật khẩu
        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        newUser.setEnabled(true); // Kích hoạt tài khoản
        // 5. Gán vai trò USER
        newUser.setRoles(Set.of(RoleName.ROLE_USER));

        // 6. Lưu vào DB
        userRepository.save(newUser);
    }
}
