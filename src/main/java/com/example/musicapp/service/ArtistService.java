package com.example.musicapp.service;

import com.example.musicapp.dto.AdminArtistRequestDTO;
import com.example.musicapp.dto.ArtistDTO;
import com.example.musicapp.entity.Artist;
import com.example.musicapp.exception.ResourceNotFoundException;
import com.example.musicapp.repository.ArtistRepository;
import com.example.musicapp.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArtistService {

    @Autowired
    private ArtistRepository artistRepository;

    // Thêm SongRepository để kiểm tra ràng buộc
    @Autowired
    private SongRepository songRepository;

    public List<ArtistDTO> getAllArtists() {
        return artistRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ArtistDTO getArtistById(Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nghệ sĩ với ID: " + id));
        return mapToDTO(artist);
    }

    @Transactional
    public ArtistDTO createArtist(AdminArtistRequestDTO dto) {
        Artist artist = new Artist();
        artist.setName(dto.getName());
        artist.setImageUrl(dto.getImageUrl());
        Artist savedArtist = artistRepository.save(artist);
        return mapToDTO(savedArtist);
    }

    @Transactional
    public ArtistDTO updateArtist(Long id, AdminArtistRequestDTO dto) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nghệ sĩ ID: " + id));
        artist.setName(dto.getName());
        artist.setImageUrl(dto.getImageUrl());
        Artist updatedArtist = artistRepository.save(artist);
        return mapToDTO(updatedArtist);
    }

    @Transactional
    public void deleteArtist(Long id) {
        if (!artistRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy nghệ sĩ ID: " + id);
        }

        // Kiểm tra ràng buộc: Không cho xóa nếu vẫn còn bài hát
        if (songRepository.existsByArtistId(id)) {
            // Ném lỗi 400 Bad Request (sẽ bị GlobalExceptionHandler bắt)
            throw new RuntimeException("Không thể xóa nghệ sĩ vì vẫn còn bài hát liên quan.");
        }

        artistRepository.deleteById(id);
    }

    // --- Private Mapper ---
    private ArtistDTO mapToDTO(Artist artist) {
        ArtistDTO dto = new ArtistDTO();
        dto.setId(artist.getId());
        dto.setName(artist.getName());
        dto.setImageUrl(artist.getImageUrl());
        return dto;
    }
}
