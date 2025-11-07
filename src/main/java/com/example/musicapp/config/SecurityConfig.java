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


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 4. Báo cho Spring Security dùng CustomUserDetailsService của chúng ta
                .userDetailsService(customUserDetailsService)

                .authorizeHttpRequests(authorize -> authorize
                        // 1. PUBLIC (Ai cũng xem được)
                        .requestMatchers(
                                "/",
                                "/css/**",
                                "/js/**",
                                "/error/**",
                                "/login", // Trang login
                                "/register", // Trang register
                                "/songs/play/**",
                                "/artists/photo/**"
                        ).permitAll()
                        // 2. ADMIN ONLY (Chỉ Admin được C/U/D)
                        .requestMatchers(
                                "/songs/create", "/songs/edit/**", "/songs/update", "/songs/delete/**",
                                "/artists/create", "/artists/edit/**", "/artists/update", "/artists/delete/**",
                                "/genres/create", "/genres/edit/**", "/genres/update", "/genres/delete/**"
                        ).hasRole("ADMIN")
                        // 3. USER (Phải đăng nhập)
                        // Bất kỳ ai đã đăng nhập (USER hoặc ADMIN) đều có thể XEM (GET) danh sách
                        .requestMatchers(
                                HttpMethod.GET,
                                "/play/**",
                                "/dashboard",
                                "/songs",
                                "/artists",
                                "/genres"
                        ).authenticated()
                        .anyRequest().denyAll()
                )
                // Cấu hình Form Login (trỏ đến trang /login tùy chỉnh)
                .formLogin(form -> form
                        .loginPage("/login") // Báo Spring Security trang login của bạn ở đâu
                        .loginProcessingUrl("/login") // Nơi xử lý POST login
                        .defaultSuccessUrl("/dashboard", true) // Tới /songs sau khi login
                        .failureUrl("/login?error=true") // Về /login?error nếu sai
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // (URL mặc định)
                        .logoutSuccessUrl("/login?logout=true") // 6. Chuyển về /login?logout sau khi logout
                        .permitAll()
                );
        return http.build();
    }
}
