package com.example.musicapp.service;

import com.example.musicapp.dto.UserRegistrationDTO;
import com.example.musicapp.entity.Artist;
import com.example.musicapp.entity.Song;
import com.example.musicapp.entity.Account; // 1. THÊM IMPORT
import org.springframework.data.domain.Page; // 2. THÊM IMPORT
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface IUserService {
    void registerNewUser(UserRegistrationDTO dto);

    /**
     * Lấy danh sách ID bài hát yêu thích của user
     *
     * @param username Tên đăng nhập của user
     * @return Set (Tập hợp) các ID (Long) của bài hát
     */
    Set<Long> getFavoriteSongIds(String username);

    /**
     * Thêm một bài hát vào danh sách yêu thích
     *
     * @param username Tên đăng nhập của user
     * @param songId   ID của bài hát
     */
    void addFavoriteSong(String username, Long songId);

    /**
     * Xóa một bài hát khỏi danh sách yêu thích
     *
     * @param username Tên đăng nhập của user
     * @param songId   ID của bài hát
     */
    void removeFavoriteSong(String username, Long songId);

    /**
     * Lấy đầy đủ thông tin các bài hát yêu thích của user
     *
     * @param username Tên đăng nhập của user
     * @return Set (Tập hợp) các đối tượng Song
     */
    Set<Song> getFavoriteSongs(String username);

    Set<Long> getFavoriteArtistIds(String username);

    void addFavoriteArtist(String username, Long artistId);

    void removeFavoriteArtist(String username, Long artistId);

    Set<Artist> getFavoriteArtists(String username);

    Page<Account> findAll(Pageable pageable);

    long count();
}
