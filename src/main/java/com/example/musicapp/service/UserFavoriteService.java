package com.example.musicapp.service;

import com.example.musicapp.config.SecurityUtil;
import com.example.musicapp.dto.SongDTO;
import com.example.musicapp.entity.Account;
import com.example.musicapp.entity.Song;
import com.example.musicapp.entity.UserFavorite;
import com.example.musicapp.exception.ResourceNotFoundException;
import com.example.musicapp.repository.SongRepository;
import com.example.musicapp.repository.UserFavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserFavoriteService {

    @Autowired
    private UserFavoriteRepository favoriteRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private SongService songService; // Dùng lại mapper

    // Lấy danh sách yêu thích
    public List<SongDTO> getMyFavorites() {
        Account currentUser = SecurityUtil.getCurrentUserAccount();
        return favoriteRepository.findByAccountId(currentUser.getId()).stream()
                .map(fav -> songService.mapToDTO(fav.getSong()))
                .collect(Collectors.toList());
    }

    // Thêm vào yêu thích
    @Transactional
    public void addFavorite(Long songId) {
        Account currentUser = SecurityUtil.getCurrentUserAccount();
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài hát ID: " + songId));

        Optional<UserFavorite> existing = favoriteRepository.findByAccountIdAndSongId(currentUser.getId(), songId);
        if (existing.isPresent()) {
            // Đã yêu thích rồi, không làm gì cả
            return;
        }

        UserFavorite favorite = new UserFavorite();
        favorite.setAccount(currentUser);
        favorite.setSong(song);
        favoriteRepository.save(favorite);
    }

    // Xóa khỏi yêu thích
    @Transactional
    public void removeFavorite(Long songId) {
        Account currentUser = SecurityUtil.getCurrentUserAccount();

        UserFavorite favorite = favoriteRepository.findByAccountIdAndSongId(currentUser.getId(), songId)
                .orElseThrow(() -> new ResourceNotFoundException("Bài hát này không có trong danh sách yêu thích"));

        favoriteRepository.delete(favorite);
    }
}
