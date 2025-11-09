package com.example.musicapp.repository;

import com.example.musicapp.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    /**
     * Lấy tất cả playlist của một người dùng
     */
    List<Playlist> findByAccountId(Long accountId);
}
