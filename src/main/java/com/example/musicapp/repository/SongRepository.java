package com.example.musicapp.repository;

import com.example.musicapp.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    // Tự động có các phương thức CRUD

    // Ví dụ: Thêm phương thức tìm kiếm bài hát theo tên (không phân biệt hoa thường)
    // List<Song> findByNameContainingIgnoreCase(String nameKeyword);

    // Ví dụ: Thêm phương thức tìm bài hát theo Artist Id
    // List<Song> findByArtistId(Long artistId);

    // Ví dụ: Thêm phương thức tìm bài hát theo Genre Id
    // List<Song> findByGenreId(Long genreId);
    // Kiểm tra xem có bài hát nào thuộc về Artist ID này không
    boolean existsByArtistId(Long artistId);

    // Kiểm tra xem có bài hát nào thuộc về Genre ID này không
    boolean existsByGenreId(Long genreId);

}
