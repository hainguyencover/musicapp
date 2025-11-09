package com.example.musicapp.repository;

import com.example.musicapp.entity.PlaylistSong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, Long> {
    /**
     * Kiểm tra xem bài hát đã có trong playlist chưa
     */
    Optional<PlaylistSong> findByPlaylistIdAndSongId(Long playlistId, Long songId);

    /**
     * Xóa tất cả các liên kết playlist liên quan đến một bài hát (Dùng khi xóa Song)
     */
    @Modifying
    @Transactional
    void deleteAllBySongId(Long songId);
}
