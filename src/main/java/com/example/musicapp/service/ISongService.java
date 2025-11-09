package com.example.musicapp.service;

import com.example.musicapp.dto.SongDTO;
import com.example.musicapp.entity.Artist;
import com.example.musicapp.entity.Genre;
import com.example.musicapp.entity.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ISongService {
    List<Song> findAll();

    Page<Song> findAll(String keyword, Pageable pageable); // Phương thức mới cho phân trang

    Optional<Song> findById(Long id); // Hoặc Optional<SongDTO>

    // Sửa phương thức save
    Song save(SongDTO songDTO, MultipartFile songFile);

    // Sửa phương thức update (hoặc gộp vào save)
    Song update(SongDTO songDTO, MultipartFile songFile);

    void deleteById(Long id);

    // Thêm các phương thức khác nếu cần, ví dụ: tìm theo tên, nghệ sĩ,...
    // List<Song> findByNameContaining(String name);
    long count();

    /**
     * MỚI: Tìm bài hát theo ID nghệ sĩ (có phân trang).
     */
    Page<Song> findByArtistId(Long artistId, Pageable pageable);

    /**
     * MỚI: Tìm các thể loại (duy nhất) liên quan đến một nghệ sĩ.
     */
    List<Genre> findDistinctGenresByArtistId(Long artistId);

    /**
     * MỚI: Tìm bài hát theo ID thể loại (có phân trang).
     */
    Page<Song> findByGenreId(Long genreId, Pageable pageable);

    /**
     * MỚI: Tìm các nghệ sĩ (duy nhất) liên quan đến một thể loại.
     */
    List<Artist> findDistinctArtistsByGenreId(Long genreId);
}
