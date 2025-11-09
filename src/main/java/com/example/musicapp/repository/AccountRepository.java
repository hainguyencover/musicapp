package com.example.musicapp.repository;

import com.example.musicapp.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Tìm tài khoản bằng email (dùng cho đăng nhập).
     * Email là unique.
     */
    Optional<Account> findByEmail(String email);

    /**
     * Kiểm tra xem email đã tồn tại hay chưa (dùng cho đăng ký).
     */
    Boolean existsByEmail(String email);
}
