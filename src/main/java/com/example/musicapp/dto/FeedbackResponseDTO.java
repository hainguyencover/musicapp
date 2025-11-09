package com.example.musicapp.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FeedbackResponseDTO {
    private Long id;
    private String content;
    private String status;
    private LocalDateTime createdAt;
    private String userEmail; // Hiển thị email người gửi
}
