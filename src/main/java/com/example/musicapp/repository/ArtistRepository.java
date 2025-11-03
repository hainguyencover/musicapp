package com.example.musicapp.repository;

import com.example.musicapp.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Integer> {
    // Spring Data JPA sẽ tự động tạo ra các phương thức CRUD cơ bản:
    // - save(Artist entity)
    // - findById(Long id)
    // - findAll()
    // - deleteById(Long id)
    // - ...

    // Bạn có thể thêm các phương thức truy vấn tùy chỉnh ở đây nếu cần,
    // ví dụ: tìm Artist theo tên
    // Optional<Artist> findByName(String name);
}
