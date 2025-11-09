package com.example.musicapp.controller;

import com.example.musicapp.dto.GenreDTO;
import com.example.musicapp.service.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    @Autowired
    private GenreService genreService;

    // GET /api/genres
    @GetMapping
    public ResponseEntity<List<GenreDTO>> getAllGenres() {
        return ResponseEntity.ok(genreService.getAllGenres());
    }

    // GET /api/genres/1
    @GetMapping("/{id}")
    public ResponseEntity<GenreDTO> getGenreById(@PathVariable Long id) {
        return ResponseEntity.ok(genreService.getGenreById(id));
    }
}
