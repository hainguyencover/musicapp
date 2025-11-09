package com.example.musicapp.controller;

import com.example.musicapp.dto.PlaylistDTO;
import com.example.musicapp.dto.PlaylistRequestDTO;
import com.example.musicapp.dto.PlaylistSongRequestDTO;
import com.example.musicapp.service.PlaylistService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    @Autowired
    private PlaylistService playlistService;

    // GET /api/playlists (Lấy playlist CỦA TÔI)
    @GetMapping
    public ResponseEntity<List<PlaylistDTO>> getMyPlaylists() {
        return ResponseEntity.ok(playlistService.getMyPlaylists());
    }

    // GET /api/playlists/1
    @GetMapping("/{id}")
    public ResponseEntity<PlaylistDTO> getPlaylistById(@PathVariable Long id) {
        // Service sẽ kiểm tra quyền sở hữu (403) hoặc 404
        return ResponseEntity.ok(playlistService.getPlaylistById(id));
    }

    // POST /api/playlists
    @PostMapping
    public ResponseEntity<PlaylistDTO> createPlaylist(@Valid @RequestBody PlaylistRequestDTO request) {
        PlaylistDTO createdPlaylist = playlistService.createPlaylist(request);
        return new ResponseEntity<>(createdPlaylist, HttpStatus.CREATED);
    }

    // DELETE /api/playlists/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable Long id) {
        playlistService.deletePlaylist(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // POST /api/playlists/1/songs (Thêm bài hát)
    @PostMapping("/{playlistId}/songs")
    public ResponseEntity<PlaylistDTO> addSongToPlaylist(
            @PathVariable Long playlistId,
            @Valid @RequestBody PlaylistSongRequestDTO request) {

        PlaylistDTO updatedPlaylist = playlistService.addSongToPlaylist(playlistId, request.getSongId());
        return ResponseEntity.ok(updatedPlaylist);
    }

    // DELETE /api/playlists/1/songs/5 (Xóa bài hát)
    @DeleteMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<PlaylistDTO> removeSongFromPlaylist(
            @PathVariable Long playlistId,
            @PathVariable Long songId) {

        PlaylistDTO updatedPlaylist = playlistService.removeSongFromPlaylist(playlistId, songId);
        return ResponseEntity.ok(updatedPlaylist);
    }
}
