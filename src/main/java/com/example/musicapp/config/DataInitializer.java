package com.example.musicapp.config;

import com.example.musicapp.entity.RoleName;
import com.example.musicapp.entity.User;
import com.example.musicapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Kiểm tra xem user 'admin' đã tồn tại chưa
        if (!userRepository.existsByUsernameIgnoreCase("admin")) {
            logger.info("Tạo tài khoản ADMIN mặc định (admin/admin123)");

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEnabled(true);
            admin.setRoles(Set.of(RoleName.ROLE_ADMIN, RoleName.ROLE_USER));

            userRepository.save(admin);
        }

        if (!userRepository.existsByUsernameIgnoreCase("user")) {
            logger.info("Tạo tài khoản USER mặc định (user/123)");
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("123"));
            user.setEnabled(true);
            user.setRoles(Set.of(RoleName.ROLE_USER));

            userRepository.save(user);
        }
    }
}
