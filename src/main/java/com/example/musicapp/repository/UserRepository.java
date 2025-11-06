package com.example.musicapp.repository;

import com.example.musicapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Tìm kiếm User dựa trên username (không phân biệt hoa thường).
     */
    Optional<User> findByUsernameIgnoreCase(String username);
}
