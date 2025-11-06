package com.example.musicapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // --- Cung cấp User (Ví dụ: lưu trong bộ nhớ - KHÔNG DÙNG CHO PRODUCTION) ---
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder().encode("123"))
                .roles("USER")
                .build();
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin123"))
                .roles("ADMIN", "USER")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Cho phép truy cập công khai CSS, JS, trang chủ, play nhạc, ảnh nghệ sĩ, trang lỗi
                        .requestMatchers("/css/**", "/js/**", "/", "/songs/play/**", "/artists/photo/**", "/error/**").permitAll()

                        // Yêu cầu ADMIN cho các chức năng xóa (quy tắc này vẫn đúng)
                        .requestMatchers("/songs/delete/**", "/artists/delete/**", "/genres/delete/**").hasRole("ADMIN")

                        // SỬA LỖI ƯU TIÊN 6: Tách bạch quyền xem và quyền sửa đổi

                        // 1. Yêu cầu ADMIN cho các hành động C/U (Create/Update) của Artist và Genre
                        .requestMatchers("/artists/create", "/artists/edit/**", "/artists/update").hasRole("ADMIN")
                        .requestMatchers("/genres/create", "/genres/edit/**", "/genres/update").hasRole("ADMIN")

                        // 2. Cho phép mọi USER đã đăng nhập được XEM (Read) danh sách
                        .requestMatchers(HttpMethod.GET, "/artists", "/genres").authenticated()

                        // Các request còn lại (ví dụ: CRUD của Song) yêu cầu phải đăng nhập (authenticated)
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
