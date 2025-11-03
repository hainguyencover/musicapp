package com.example.musicapp.repository;

import com.example.musicapp.entity.Artist;
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
}
