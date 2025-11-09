package com.example.musicapp.controller;

import com.example.musicapp.dto.SongDTO;
import com.example.musicapp.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/songs")
public class SongController {

    @Autowired
    private SongService songService;

    // GET /api/songs
    @GetMapping
    public ResponseEntity<List<SongDTO>> getAllSongs() {
        return ResponseEntity.ok(songService.getAllSongs());
    }

    // GET /api/songs/1
    @GetMapping("/{id}")
    public ResponseEntity<SongDTO> getSongById(@PathVariable Long id) {
        // Service sẽ ném 404 nếu không tìm thấy
        return ResponseEntity.ok(songService.getSongById(id));
    }

    // GET /api/songs/search?title=lac
    @GetMapping("/search")
    public ResponseEntity<List<SongDTO>> searchSongs(@RequestParam String title) {
        return ResponseEntity.ok(songService.searchSongsByTitle(title));
    }

    // GET /api/songs/by-artist/1
    @GetMapping("/by-artist/{artistId}")
    public ResponseEntity<List<SongDTO>> getSongsByArtist(@PathVariable Long artistId) {
        return ResponseEntity.ok(songService.getSongsByArtistId(artistId));
    }

    // GET /api/songs/by-genre/1
    @GetMapping("/by-genre/{genreId}")
    public ResponseEntity<List<SongDTO>> getSongsByGenre(@PathVariable Long genreId) {
        return ResponseEntity.ok(songService.getSongsByGenreId(genreId));
    }
}
