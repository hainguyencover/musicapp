package com.example.musicapp.config;

import com.example.musicapp.service.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationSuccessHandler customAuthenticationSuccessHandler;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService, AuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Dịch vụ User (Đọc từ DB)
                .userDetailsService(customUserDetailsService)

                // 2. Cấu hình Phân quyền (ĐÃ CẬP NHẬT)
                .authorizeHttpRequests(authorize -> authorize

                        // 2.1. PUBLIC (Ai cũng xem được)
                        .requestMatchers(
                                "/",
                                "/css/**",
                                "/js/**",
                                "/error/**",
                                "/login-processing", // Trang login
                                "/register", // Trang register
                                "/songs/play/**", // API phát nhạc
                                "/artists/photo/**" // API xem ảnh
                        ).permitAll()

                        // 2.2. USER (Chỉ cần đăng nhập)
                        // Đây là các trang trải nghiệm của người dùng
                        .requestMatchers(
                                "/dashboard",
                                "/play/**",
                                "/favorites",
                                "/api/favorites/**",
                                "/explore/**" // <- URL MỚI CHO USER XEM LIST
                        ).authenticated()

                        // 2.3. ADMIN ONLY (Chỉ Admin)
                        // Gộp tất cả các trang quản lý vào một quy tắc
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 2.4. Chặn mọi thứ còn lại
                        .anyRequest().denyAll()
                )

                // 3. Cấu hình Form Login
                .formLogin(form -> form
                        .loginPage("/") // Báo Spring Security trang login của bạn ở đâu
                        .loginProcessingUrl("/login-processing") // Nơi xử lý POST login
                        .successHandler(customAuthenticationSuccessHandler)
                        .failureUrl("/?error=true") // Về /login?error nếu sai
                        .permitAll()
                )

                // 4. Cấu hình Logout
                .logout(logout -> logout
                        .logoutUrl("/logout") // (URL mặc định)
                        .logoutSuccessUrl("/?logout=true") // 6. Chuyển về /login?logout sau khi logout
                        .permitAll()
                );
        return http.build();
    }
}
