package com.example.musicapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "admin_feedback")
@Getter
@Setter
public class AdminFeedback extends BaseEntity {

    @Lob
    // Thêm columnDefinition = "TEXT" để khớp chính xác với CSDL
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    // Thêm length = 20 để tối ưu (thay vì varchar(255) mặc định)
    @Column(nullable = false, length = 20)
    private String status = "PENDING"; // (PENDING, RESOLVED)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
}
