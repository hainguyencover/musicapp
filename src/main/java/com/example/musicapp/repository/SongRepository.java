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

    /**
     * MỚI: Tìm các bài hát của một nghệ sĩ (có phân trang).
     */
    Page<Song> findByArtistId(Long artistId, Pageable pageable);

    /**
     * MỚI: Tìm các thể loại nhạc (duy nhất) mà một nghệ sĩ thể hiện.
     */
    @Query("SELECT DISTINCT s.genre FROM Song s WHERE s.artist.id = :artistId")
    List<Genre> findDistinctGenresByArtistId(@Param("artistId") Long artistId);

    /**
     * MỚI: Tìm các bài hát của một thể loại (có phân trang).
     */
    Page<Song> findByGenreId(Long genreId, Pageable pageable);

    /**
     * MỚI: Tìm các nghệ sĩ (duy nhất) có bài hát trong thể loại này.
     */
    @Query("SELECT DISTINCT s.artist FROM Song s WHERE s.genre.id = :genreId")
    List<Artist> findDistinctArtistsByGenreId(@Param("genreId") Long genreId);
}
