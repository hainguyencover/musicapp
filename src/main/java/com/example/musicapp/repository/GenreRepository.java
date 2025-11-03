package com.example.musicapp.repository;

import com.example.musicapp.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    // Tự động có các phương thức CRUD
    // Có thể thêm phương thức truy vấn tùy chỉnh nếu cần
}
