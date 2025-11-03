package com.example.musicapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults; // Dùng cho formLogin mặc định

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Luôn mã hóa mật khẩu!
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // --- Cung cấp User (Ví dụ: lưu trong bộ nhớ - KHÔNG DÙNG CHO PRODUCTION) ---
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder().encode("123")) // Mã hóa mật khẩu
                .roles("USER")
                .build();
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin123"))
                .roles("ADMIN", "USER") // Admin có cả 2 vai trò
                .build();
        // Trong thực tế, bạn sẽ tạo implementation đọc user từ Database
        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        // Cho phép truy cập công khai CSS, JS, trang chủ, play nhạc, trang lỗi
                        .requestMatchers("/css/**", "/js/**", "/", "/songs/play/**", "/error/**").permitAll()
                        // Yêu cầu ADMIN cho các chức năng xóa
                        .requestMatchers("/songs/delete/**", "/artists/delete/**", "/genres/delete/**").hasRole("ADMIN")
                        // Yêu cầu ADMIN cho trang tạo/sửa Artist, Genre (ví dụ)
                        .requestMatchers("/artists/**", "/genres/**").hasRole("ADMIN") // Rút gọn lại
                        // Các request còn lại yêu cầu phải đăng nhập
                        .anyRequest().authenticated()
                )
                // Bật form login mặc định
                .formLogin(withDefaults())
                .logout(logout -> logout // Cấu hình logout
                        .logoutSuccessUrl("/") // Về trang chủ sau logout
                        .permitAll()
                );
        return http.build();
    }
}
