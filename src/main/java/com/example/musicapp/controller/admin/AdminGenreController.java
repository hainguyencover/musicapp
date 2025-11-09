package com.example.musicapp.controller.admin;

import com.example.musicapp.dto.AdminGenreRequestDTO;
import com.example.musicapp.dto.GenreDTO;
import com.example.musicapp.service.GenreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/genres")
@PreAuthorize("hasRole('ADMIN')") // --- BẮT BUỘC QUYỀN ADMIN ---
public class AdminGenreController {

    @Autowired
    private GenreService genreService;

    @PostMapping
    public ResponseEntity<GenreDTO> createGenre(@Valid @RequestBody AdminGenreRequestDTO request) {
        GenreDTO createdGenre = genreService.createGenre(request);
        return new ResponseEntity<>(createdGenre, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenreDTO> updateGenre(@PathVariable Long id, @Valid @RequestBody AdminGenreRequestDTO request) {
        GenreDTO updatedGenre = genreService.updateGenre(id, request);
        return ResponseEntity.ok(updatedGenre);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable Long id) {
        genreService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }
}
