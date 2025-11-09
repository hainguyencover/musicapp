package com.example.musicapp.repository;

import com.example.musicapp.entity.Artist;
import com.example.musicapp.entity.Genre;
import com.example.musicapp.entity.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    // Tìm bài hát theo tiêu đề (cho chức năng tìm kiếm)
    List<Song> findByTitleContainingIgnoreCase(String title);

    // Tìm bài hát theo ID nghệ sĩ
    List<Song> findByArtistId(Long artistId);

    // Tìm bài hát theo ID thể loại
    List<Song> findByGenreId(Long genreId);

    boolean existsByGenreId(Long genreId);

    boolean existsByArtistId(Long artistId);

}
