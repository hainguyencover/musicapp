package com.example.musicapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing // Kích hoạt tính năng tự động điền timestamp
public class JpaAuditingConfig {
    // Lớp này chỉ cần tồn tại để kích hoạt @EnableJpaAuditing
}
