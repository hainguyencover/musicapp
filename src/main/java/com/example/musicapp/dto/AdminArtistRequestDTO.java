package com.example.musicapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class AdminArtistRequestDTO {

    @NotBlank(message = "Tên nghệ sĩ không được để trống")
    private String name;

    @NotBlank(message = "Đường dẫn ảnh không được để trống")
    @URL(message = "Đường dẫn ảnh không hợp lệ")
    private String imageUrl;
}
