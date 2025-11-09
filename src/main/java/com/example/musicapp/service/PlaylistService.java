package com.example.musicapp.service;

import com.example.musicapp.config.SecurityUtil;
import com.example.musicapp.dto.PlaylistDTO;
import com.example.musicapp.dto.PlaylistRequestDTO;
import com.example.musicapp.dto.SongDTO;
import com.example.musicapp.entity.Account;
import com.example.musicapp.entity.Playlist;
import com.example.musicapp.entity.PlaylistSong;
import com.example.musicapp.entity.Song;
import com.example.musicapp.exception.ForbiddenException;
import com.example.musicapp.exception.ResourceNotFoundException;
import com.example.musicapp.repository.PlaylistRepository;
import com.example.musicapp.repository.PlaylistSongRepository;
import com.example.musicapp.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaylistService {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private PlaylistSongRepository playlistSongRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private SongService songService; // Dùng lại mapper của SongService

    // Lấy tất cả playlist của người dùng hiện tại
    public List<PlaylistDTO> getMyPlaylists() {
        Account currentUser = SecurityUtil.getCurrentUserAccount();
        return playlistRepository.findByAccountId(currentUser.getId()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Lấy chi tiết 1 playlist
    public PlaylistDTO getPlaylistById(Long playlistId) {
        Playlist playlist = getPlaylistAndVerifyOwner(playlistId);
        return mapToDTO(playlist);
    }

    // Tạo playlist mới
    @Transactional
    public PlaylistDTO createPlaylist(PlaylistRequestDTO request) {
        Account currentUser = SecurityUtil.getCurrentUserAccount();
        Playlist playlist = new Playlist();
        playlist.setName(request.getName());
        playlist.setAccount(currentUser);

        Playlist savedPlaylist = playlistRepository.save(playlist);
        return mapToDTO(savedPlaylist);
    }

    // Xóa playlist
    @Transactional
    public void deletePlaylist(Long playlistId) {
        Playlist playlist = getPlaylistAndVerifyOwner(playlistId);

        // Xóa các liên kết trong bảng playlist_songs trước
        playlistSongRepository.deleteAll(playlist.getPlaylistSongs());

        // Xóa playlist
        playlistRepository.delete(playlist);
    }

    // Thêm bài hát vào playlist
    @Transactional
    public PlaylistDTO addSongToPlaylist(Long playlistId, Long songId) {
        Playlist playlist = getPlaylistAndVerifyOwner(playlistId);
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài hát với ID: " + songId));

        // Kiểm tra bài hát đã tồn tại trong playlist chưa
        if (playlistSongRepository.findByPlaylistIdAndSongId(playlistId, songId).isPresent()) {
            throw new RuntimeException("Bài hát đã có trong playlist");
        }

        PlaylistSong playlistSong = new PlaylistSong();
        playlistSong.setPlaylist(playlist);
        playlistSong.setSong(song);
        playlistSongRepository.save(playlistSong);

        // Tải lại playlist để trả về DTO đầy đủ
        return getPlaylistById(playlistId);
    }

    // Xóa bài hát khỏi playlist
    @Transactional
    public PlaylistDTO removeSongFromPlaylist(Long playlistId, Long songId) {
        Playlist playlist = getPlaylistAndVerifyOwner(playlistId);

        PlaylistSong playlistSong = playlistSongRepository.findByPlaylistIdAndSongId(playlistId, songId)
                .orElseThrow(() -> new ResourceNotFoundException("Bài hát không có trong playlist này"));

        playlistSongRepository.delete(playlistSong);

        return getPlaylistById(playlistId);
    }


    // --- Helper: Kiểm tra quyền sở hữu ---
    private Playlist getPlaylistAndVerifyOwner(Long playlistId) {
        Account currentUser = SecurityUtil.getCurrentUserAccount();
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy playlist với ID: " + playlistId));

        // Kiểm tra quyền
        if (!playlist.getAccount().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Bạn không có quyền truy cập playlist này");
        }
        return playlist;
    }

    // --- Helper: Mapper ---
    private PlaylistDTO mapToDTO(Playlist playlist) {
        PlaylistDTO dto = new PlaylistDTO();
        dto.setId(playlist.getId());
        dto.setName(playlist.getName());
        dto.setAccountId(playlist.getAccount().getId());

        // Lấy danh sách bài hát
        List<SongDTO> songs = playlist.getPlaylistSongs().stream()
                .map(playlistSong -> songService.mapToDTO(playlistSong.getSong()))
                .collect(Collectors.toList());
        dto.setSongs(songs);

        return dto;
    }
}
