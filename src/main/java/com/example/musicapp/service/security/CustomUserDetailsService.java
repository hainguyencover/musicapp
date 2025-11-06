package com.example.musicapp.service.security;

import com.example.musicapp.entity.User;
import com.example.musicapp.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true) // Đảm bảo transaction được mở (đặc biệt nếu FetchType là LAZY)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 1. Tìm user trong DB bằng username
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Không tìm thấy người dùng với tên đăng nhập: " + username));

        // 2. Chuyển đổi Set<RoleName> (Enum của chúng ta)
        //    thành Set<GrantedAuthority> (của Spring Security)
        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(roleName -> new SimpleGrantedAuthority(roleName.name()))
                .collect(Collectors.toSet());

        // 3. Trả về đối tượng UserDetails mà Spring Security hiểu
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities // Danh sách quyền
        );
    }
}
