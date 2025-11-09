package com.example.musicapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminGenreRequestDTO {
    @NotBlank(message = "Tên thể loại không được để trống")
    private String name;
}
