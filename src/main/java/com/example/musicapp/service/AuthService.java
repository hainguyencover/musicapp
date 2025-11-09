package com.example.musicapp.service;

import com.example.musicapp.config.jwt.JwtTokenProvider;
import com.example.musicapp.dto.LoginRequest;
import com.example.musicapp.dto.LoginResponse;
import com.example.musicapp.dto.RegisterRequest;
import com.example.musicapp.entity.Account;
import com.example.musicapp.entity.Role;
import com.example.musicapp.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Xử lý đăng nhập
     */
    public LoginResponse login(LoginRequest loginRequest) {
        // Xác thực email và password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // Nếu xác thực thành công, đưa vào SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Tạo JWT token
        String token = tokenProvider.generateToken(authentication);

        // Lấy thông tin user để trả về
        Account account = accountRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy user sau khi đăng nhập"));

        return new LoginResponse(token, account.getEmail(), account.getDisplayName(), account.getRole().name());
    }

    /**
     * Xử lý đăng ký
     */
    public Account register(RegisterRequest registerRequest) {
        if (accountRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Lỗi: Email đã được sử dụng!");
        }

        Account account = new Account();
        account.setEmail(registerRequest.getEmail());
        account.setDisplayName(registerRequest.getDisplayName());

        // Mã hóa mật khẩu
        // Mật khẩu '123456' trong init.sql được mã hóa bằng BCrypt
        // $2a$10$f.08/A.A.0jJ2.9Q3.x2fO1iZ.0.a.7.a.U.a.9.a.U.a.9.a.9
        account.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // Mặc định là USER
        account.setRole(Role.USER);

        return accountRepository.save(account);
    }
}
