package com.example.musicapp.controller;

import com.example.musicapp.dto.FavoriteRequestDTO;
import com.example.musicapp.dto.SongDTO;
import com.example.musicapp.service.UserFavoriteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class UserFavoriteController {

    @Autowired
    private UserFavoriteService favoriteService;

    // GET /api/favorites (Lấy danh sách yêu thích CỦA TÔI)
    @GetMapping
    public ResponseEntity<List<SongDTO>> getMyFavorites() {
        return ResponseEntity.ok(favoriteService.getMyFavorites());
    }

    // POST /api/favorites
    @PostMapping
    public ResponseEntity<Void> addFavorite(@Valid @RequestBody FavoriteRequestDTO request) {
        favoriteService.addFavorite(request.getSongId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // DELETE /api/favorites/5 (Xóa bài hát 5 khỏi yêu thích)
    @DeleteMapping("/{songId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long songId) {
        favoriteService.removeFavorite(songId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
