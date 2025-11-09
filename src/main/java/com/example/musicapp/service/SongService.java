package com.example.musicapp.service;

import com.example.musicapp.dto.AdminSongRequestDTO;
import com.example.musicapp.dto.ArtistDTO;
import com.example.musicapp.dto.GenreDTO;
import com.example.musicapp.dto.SongDTO;
import com.example.musicapp.entity.Artist;
import com.example.musicapp.entity.Genre;
import com.example.musicapp.entity.Song;
import com.example.musicapp.exception.ResourceNotFoundException;
import com.example.musicapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SongService {

    @Autowired
    private SongRepository songRepository;

    // Thêm repositories để tạo/cập nhật Song
    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private GenreRepository genreRepository;

    // Thêm repositories để xóa dependencies
    @Autowired
    private PlaylistSongRepository playlistSongRepository;

    @Autowired
    private UserFavoriteRepository userFavoriteRepository;

    @Autowired
    private ListeningHistoryRepository listeningHistoryRepository;

    public List<SongDTO> getAllSongs() {
        return songRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public SongDTO getSongById(Long id) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài hát với ID: " + id));
        return mapToDTO(song);
    }

    public List<SongDTO> searchSongsByTitle(String title) {
        return songRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<SongDTO> getSongsByArtistId(Long artistId) {
        return songRepository.findByArtistId(artistId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<SongDTO> getSongsByGenreId(Long genreId) {
        return songRepository.findByGenreId(genreId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SongDTO createSong(AdminSongRequestDTO dto) {
        Artist artist = artistRepository.findById(dto.getArtistId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nghệ sĩ ID: " + dto.getArtistId()));
        Genre genre = genreRepository.findById(dto.getGenreId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thể loại ID: " + dto.getGenreId()));

        Song song = new Song();
        song.setTitle(dto.getTitle());
        song.setDuration(dto.getDuration());
        song.setFileUrl(dto.getFileUrl());
        song.setImageUrl(dto.getImageUrl());
        song.setArtist(artist);
        song.setGenre(genre);

        Song savedSong = songRepository.save(song);
        return mapToDTO(savedSong);
    }

    @Transactional
    public SongDTO updateSong(Long id, AdminSongRequestDTO dto) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài hát ID: " + id));

        Artist artist = artistRepository.findById(dto.getArtistId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nghệ sĩ ID: " + dto.getArtistId()));
        Genre genre = genreRepository.findById(dto.getGenreId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thể loại ID: " + dto.getGenreId()));

        song.setTitle(dto.getTitle());
        song.setDuration(dto.getDuration());
        song.setFileUrl(dto.getFileUrl());
        song.setImageUrl(dto.getImageUrl());
        song.setArtist(artist);
        song.setGenre(genre);

        Song updatedSong = songRepository.save(song);
        return mapToDTO(updatedSong);
    }

    @Transactional
    public void deleteSong(Long id) {
        if (!songRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy bài hát ID: " + id);
        }

        // 1. Xóa dependencies (Rất quan trọng)
        playlistSongRepository.deleteAllBySongId(id);
        userFavoriteRepository.deleteAllBySongId(id);
        listeningHistoryRepository.deleteAllBySongId(id);

        // 2. Xóa bài hát
        songRepository.deleteById(id);
    }

    // --- Private Mapper ---
    // (Trong dự án lớn có thể dùng MapStruct, nhưng thủ công thế này rõ ràng hơn)
    SongDTO mapToDTO(Song song) {
        SongDTO dto = new SongDTO();
        dto.setId(song.getId());
        dto.setTitle(song.getTitle());
        dto.setDuration(song.getDuration());
        dto.setFileUrl(song.getFileUrl());
        dto.setImageUrl(song.getImageUrl());

        // Map nghệ sĩ
        Artist artist = song.getArtist();
        if (artist != null) {
            ArtistDTO artistDTO = new ArtistDTO();
            artistDTO.setId(artist.getId());
            artistDTO.setName(artist.getName());
            artistDTO.setImageUrl(artist.getImageUrl());
            dto.setArtist(artistDTO);
        }

        // Map thể loại
        Genre genre = song.getGenre();
        if (genre != null) {
            GenreDTO genreDTO = new GenreDTO();
            genreDTO.setId(genre.getId());
            genreDTO.setName(genre.getName());
            dto.setGenre(genreDTO);
        }

        return dto;
    }
}
