package com.example.musicapp.repository;

import com.example.musicapp.entity.ListeningHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface ListeningHistoryRepository extends JpaRepository<ListeningHistory, Long> {

    /**
     * Lấy lịch sử nghe nhạc của người dùng, sắp xếp theo thời gian mới nhất
     * (Hỗ trợ phân trang)
     */
    Page<ListeningHistory> findByAccountIdOrderByListenedAtDesc(Long accountId, Pageable pageable);

    /**
     * Xóa tất cả các liên kết playlist liên quan đến một bài hát (Dùng khi xóa Song)
     */
    @Modifying
    @Transactional
    void deleteAllBySongId(Long songId);
}
