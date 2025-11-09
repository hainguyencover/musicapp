package com.example.musicapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class AdminSongRequestDTO {

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    @Min(value = 1, message = "Thời lượng phải lớn hơn 0")
    private int duration;

    @NotBlank(message = "Đường dẫn file không được để trống")
    @URL(message = "Đường dẫn file không hợp lệ")
    private String fileUrl;

    @NotBlank(message = "Đường dẫn ảnh không được để trống")
    @URL(message = "Đường dẫn ảnh không hợp lệ")
    private String imageUrl;

    @NotNull(message = "ID nghệ sĩ không được để trống")
    private Long artistId;

    @NotNull(message = "ID thể loại không được để trống")
    private Long genreId;
}
