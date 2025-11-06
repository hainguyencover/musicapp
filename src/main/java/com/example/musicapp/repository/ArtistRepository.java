package com.example.musicapp.repository;

import com.example.musicapp.entity.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
/**
     * Kiểm tra tên tồn tại (không phân biệt hoa thường) - Dùng khi tạo mới.
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Kiểm tra tên tồn tại (không phân biệt hoa thường) VÀ ID khác với ID hiện tại
     * - Dùng khi cập nhật.
     */
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    /**
     * Tìm kiếm nghệ sĩ theo tên (không phân biệt hoa thường, tìm kiếm một phần).
     * @param name Tên nghệ sĩ (hoặc một phần tên)
     * @param pageable Thông tin phân trang
     * @return Một trang (Page) các nghệ sĩ
     */
    Page<Artist> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
