package com.example.musicapp.repository;

import com.example.musicapp.entity.AdminFeedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminFeedbackRepository extends JpaRepository<AdminFeedback, Long> {
    List<AdminFeedback> findAllByOrderByStatusAscCreatedAtDesc();

    /**
     * Lấy tất cả feedback, sắp xếp theo ngày tạo mới nhất (mặc định)
     */
    Page<AdminFeedback> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Lấy tất cả feedback, ưu tiên PENDING (Status A-Z), sau đó theo ngày tạo mới nhất
     * (PENDING sẽ lên trước RESOLVED)
     */
    Page<AdminFeedback> findAllByOrderByStatusAscCreatedAtDesc(Pageable pageable);
}
