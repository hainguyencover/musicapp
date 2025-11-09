package com.example.musicapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PlaylistSongRequestDTO {
    @NotNull(message = "ID bài hát không được để trống")
    private Long songId;
}
