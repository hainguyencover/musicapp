package com.example.musicapp.repository;

import com.example.musicapp.entity.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
    /**
     * Tìm kiếm thể loại theo tên (không phân biệt hoa thường, tìm kiếm một phần).
     * @param name Tên thể loại (hoặc một phần tên)
     * @param pageable Thông tin phân trang
     * @return Một trang (Page) các thể loại
     */
    Page<Genre> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
