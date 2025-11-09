package com.example.musicapp.controller.admin;

import com.example.musicapp.dto.AdminSongRequestDTO;
import com.example.musicapp.dto.SongDTO;
import com.example.musicapp.service.SongService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/songs")
@PreAuthorize("hasRole('ADMIN')") // --- BẮT BUỘC QUYỀN ADMIN ---
public class AdminSongController {

    @Autowired
    private SongService songService;

    @PostMapping
    public ResponseEntity<SongDTO> createSong(@Valid @RequestBody AdminSongRequestDTO request) {
        SongDTO createdSong = songService.createSong(request);
        return new ResponseEntity<>(createdSong, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SongDTO> updateSong(@PathVariable Long id, @Valid @RequestBody AdminSongRequestDTO request) {
        SongDTO updatedSong = songService.updateSong(id, request);
        return ResponseEntity.ok(updatedSong);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable Long id) {
        songService.deleteSong(id);
        return ResponseEntity.noContent().build();
    }
}
