package com.example.musicapp.service.impl;

import com.example.musicapp.entity.Genre;
import com.example.musicapp.entity.Song;
import com.example.musicapp.exception.DeletionBlockedException;
import com.example.musicapp.exception.DuplicateNameException;
import com.example.musicapp.repository.GenreRepository;
import com.example.musicapp.repository.SongRepository;
import com.example.musicapp.service.IGenreService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GenreServiceImpl implements IGenreService {
    private final GenreRepository genreRepository;
    private final SongRepository songRepository; // Inject SongRepository

    public GenreServiceImpl(GenreRepository genreRepository, SongRepository songRepository) {
        this.genreRepository = genreRepository;
        this.songRepository = songRepository;
    }

    @Override
    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    @Override
    public Page<Genre> findAll(String keyword, Pageable pageable) { // Implement phương thức mới
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Nếu có keyword, gọi phương thức tìm kiếm
            return genreRepository.findByNameContainingIgnoreCase(keyword, pageable);
        } else {
            // Nếu không có keyword, gọi findAll mặc định
            return genreRepository.findAll(pageable);
        }
    }

    @Override
    public Optional<Genre> findById(Long id) {
        return genreRepository.findById(id);
    }

    @Override
    @Transactional
    public Genre save(Genre genre) {
        boolean nameExists;
        if (genre.getId() == null) {
            // TẠO MỚI
            nameExists = genreRepository.existsByNameIgnoreCase(genre.getName());
        } else {
            // CẬP NHẬT
            nameExists = genreRepository.existsByNameIgnoreCaseAndIdNot(genre.getName(), genre.getId());
        }

        if (nameExists) {
            // NÉM EXCEPTION
            throw new DuplicateNameException("Tên thể loại '" + genre.getName() + "' đã tồn tại.");
        }
        return genreRepository.save(genre);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        // Kiểm tra xem có bài hát nào liên kết không
        if (songRepository.existsByGenreId(id)) {
            // Nếu có, ném exception
            throw new DeletionBlockedException("Không thể xóa thể loại này vì có bài hát đang liên kết.");
            // Hoặc: throw new DataIntegrityViolationException("Cannot delete Genre with associated Songs.");
        }
        // Chỉ xóa nếu không có bài hát nào liên kết
        genreRepository.deleteById(id);
    }

    @Override
    public long count() {
        // Chỉ cần gọi JpaRepository.count()
        return genreRepository.count();
    }
}
