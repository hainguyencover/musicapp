package com.example.musicapp.controller;

import com.example.musicapp.dto.ArtistDTO;
import com.example.musicapp.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
public class ArtistController {

    @Autowired
    private ArtistService artistService;

    // GET /api/artists
    @GetMapping
    public ResponseEntity<List<ArtistDTO>> getAllArtists() {
        return ResponseEntity.ok(artistService.getAllArtists());
    }

    // GET /api/artists/1
    @GetMapping("/{id}")
    public ResponseEntity<ArtistDTO> getArtistById(@PathVariable Long id) {
        return ResponseEntity.ok(artistService.getArtistById(id));
    }
}
