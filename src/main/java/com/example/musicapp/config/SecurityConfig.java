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

import static org.springframework.security.config.Customizer.withDefaults; // Dùng cho formLogin mặc định

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
                        // (Các quy tắc phân quyền giữ nguyên)
                        .requestMatchers(
                                "/", "/css/**", "/js/**", "/error/**",
                                "/songs/play/**", "/artists/photo/**"
                        ).permitAll()
                        .requestMatchers(
                                "/songs/create", "/songs/edit/**", "/songs/update", "/songs/delete/**",
                                "/artists/create", "/artists/edit/**", "/artists/update", "/artists/delete/**",
                                "/genres/create", "/genres/edit/**", "/genres/update", "/genres/delete/**"
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.GET,
                                "/songs", "/artists", "/genres"
                        ).authenticated()
                        .anyRequest().denyAll()
                )
                .formLogin(withDefaults())
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                );
        return http.build();
    }
}
