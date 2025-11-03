package com.example.musicapp.repository;

import com.example.musicapp.entity.Song;
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
}
