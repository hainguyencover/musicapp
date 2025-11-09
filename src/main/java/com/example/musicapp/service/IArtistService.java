package com.example.musicapp.service;

import com.example.musicapp.entity.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IArtistService {
    List<Artist> findAll();

    Page<Artist> findAll(String keyword, Pageable pageable);

    Optional<Artist> findById(Long id);

    Artist save(Artist artist); // Dùng cho cả tạo mới và cập nhật

    void deleteById(Long id);

    long count();
    // Có thể thêm các phương thức nghiệp vụ khác nếu cần
}
