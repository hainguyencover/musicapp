package com.example.musicapp.service;

import com.example.musicapp.dto.AdminGenreRequestDTO;
import com.example.musicapp.dto.GenreDTO;
import com.example.musicapp.entity.Genre;
import com.example.musicapp.exception.ResourceNotFoundException;
import com.example.musicapp.repository.GenreRepository;
import com.example.musicapp.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GenreService {

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private SongRepository songRepository;

    public List<GenreDTO> getAllGenres() {
        return genreRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public GenreDTO getGenreById(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thể loại với ID: " + id));
        return mapToDTO(genre);
    }

    @Transactional
    public GenreDTO createGenre(AdminGenreRequestDTO dto) {
        Genre genre = new Genre();
        genre.setName(dto.getName());
        Genre savedGenre = genreRepository.save(genre);
        return mapToDTO(savedGenre);
    }

    @Transactional
    public GenreDTO updateGenre(Long id, AdminGenreRequestDTO dto) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thể loại ID: " + id));
        genre.setName(dto.getName());
        Genre updatedGenre = genreRepository.save(genre);
        return mapToDTO(updatedGenre);
    }

    @Transactional
    public void deleteGenre(Long id) {
        if (!genreRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy thể loại ID: " + id);
        }

        // Kiểm tra ràng buộc: Không cho xóa nếu vẫn còn bài hát
        if (songRepository.existsByGenreId(id)) {
            // Ném lỗi 400 Bad Request
            throw new RuntimeException("Không thể xóa thể loại vì vẫn còn bài hát liên quan.");
        }

        genreRepository.deleteById(id);
    }

    // --- Private Mapper ---
    private GenreDTO mapToDTO(Genre genre) {
        GenreDTO dto = new GenreDTO();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        return dto;
    }
}
