package com.example.musicapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FavoriteRequestDTO {
    @NotNull(message = "ID bài hát không được để trống")
    private Long songId;
}
