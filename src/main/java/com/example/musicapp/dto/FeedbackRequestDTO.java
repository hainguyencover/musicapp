package com.example.musicapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FeedbackRequestDTO {
    @NotBlank(message = "Nội dung phản hồi không được để trống")
    private String content;
}
