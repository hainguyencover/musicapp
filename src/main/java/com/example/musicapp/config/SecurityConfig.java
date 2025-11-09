package com.example.musicapp.config;

import com.example.musicapp.config.jwt.JwtAuthFilter;
import com.example.musicapp.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Cho phép dùng @PreAuthorize (nếu cần)
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    // Bean mã hóa mật khẩu
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean quản lý xác thực
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Bean cung cấp provider (kết hợp UserDetailsService và PasswordEncoder)
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // Cấu hình chuỗi filter bảo mật
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Tắt CSRF (vì dùng JWT, không dựa trên session/cookie)
                .csrf(csrf -> csrf.disable())

                // Bật CORS (theo cấu hình trong WebConfig)
                .cors(cors -> {
                })

                // Cấu hình Session: Không sử dụng session (STATELESS)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Phân quyền truy cập
                .authorizeHttpRequests(authz -> authz
                        // Cho phép tất cả truy cập vào API đăng nhập, đăng ký
                        .requestMatchers("/api/auth/**").permitAll()
                        // Cho phép truy cập công khai vào API (ví dụ: lấy bài hát, tìm kiếm...)
                        // Tạm thời mở rộng, sau này sẽ siết lại
                        .requestMatchers("/api/songs/**", "/api/artists/**", "/api/genres/**").permitAll()
                        // Yêu cầu ADMIN cho các API quản trị
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // Tất cả các request còn lại phải được xác thực
                        .anyRequest().authenticated()
                )

                // Đăng ký provider
                .authenticationProvider(authenticationProvider())

                // Thêm filter JWT vào trước filter UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
