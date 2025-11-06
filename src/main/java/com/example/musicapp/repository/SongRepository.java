package com.example.musicapp.repository;

import com.example.musicapp.entity.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    // Kiểm tra xem có bài hát nào thuộc về Artist ID này không
    boolean existsByArtistId(Long artistId);

    // Kiểm tra xem có bài hát nào thuộc về Genre ID này không
    boolean existsByGenreId(Long genreId);

    boolean existsByNameIgnoreCaseAndArtistId(String name, Long artistId);

    boolean existsByNameIgnoreCaseAndArtistIdAndIdNot(String name, Long artistId, Long songId);

    /**
     * Tìm kiếm bài hát theo tên (không phân biệt hoa thường, tìm kiếm một phần).
     *
     * @param name     Tên bài hát (keyword)
     * @param pageable Thông tin phân trang
     * @return Một trang (Page) các bài hát
     */
    Page<Song> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
