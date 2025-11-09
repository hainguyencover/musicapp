package com.example.musicapp.controller.admin;

import com.example.musicapp.dto.AdminArtistRequestDTO;
import com.example.musicapp.dto.ArtistDTO;
import com.example.musicapp.service.ArtistService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/artists")
@PreAuthorize("hasRole('ADMIN')") // --- BẮT BUỘC QUYỀN ADMIN ---
public class AdminArtistController {

    @Autowired
    private ArtistService artistService;

    @PostMapping
    public ResponseEntity<ArtistDTO> createArtist(@Valid @RequestBody AdminArtistRequestDTO request) {
        ArtistDTO createdArtist = artistService.createArtist(request);
        return new ResponseEntity<>(createdArtist, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArtistDTO> updateArtist(@PathVariable Long id, @Valid @RequestBody AdminArtistRequestDTO request) {
        ArtistDTO updatedArtist = artistService.updateArtist(id, request);
        return ResponseEntity.ok(updatedArtist);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable Long id) {
        artistService.deleteArtist(id);
        return ResponseEntity.noContent().build();
    }
}
