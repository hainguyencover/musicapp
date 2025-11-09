package com.example.musicapp.repository;

import com.example.musicapp.entity.UserFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFavoriteRepository extends JpaRepository<UserFavorite, Long> {

    /**
     * Tìm danh sách bài hát yêu thích của một người dùng
     */
    List<UserFavorite> findByAccountId(Long accountId);

    /**
     * Kiểm tra xem một bài hát cụ thể đã được yêu thích bởi người dùng chưa
     */
    Optional<UserFavorite> findByAccountIdAndSongId(Long accountId, Long songId);

    /**
     * Xóa tất cả các liên kết playlist liên quan đến một bài hát (Dùng khi xóa Song)
     */
    @Modifying
    @Transactional
    void deleteAllBySongId(Long songId);
}
