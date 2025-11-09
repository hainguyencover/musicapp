package com.example.musicapp.repository;

import com.example.musicapp.entity.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    // Spring Data JPA sẽ tự động cung cấp các phương thức CRUD
}
