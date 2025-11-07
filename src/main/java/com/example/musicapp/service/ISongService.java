package com.example.musicapp.service;

import com.example.musicapp.dto.SongDTO;
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
}
