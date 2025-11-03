package com.example.musicapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class SongDTO {
    private Long id;

    @NotBlank(message = "Tên bài hát không được để trống")
    @Size(max = 255, message = "Tên bài hát không quá 255 ký tự")
    private String name;

    @NotNull(message = "Vui lòng chọn nghệ sĩ")
    private Long artistId; // Chỉ cần ID để nhận từ form

    @NotNull(message = "Vui lòng chọn thể loại")
    private Long genreId; // Chỉ cần ID để nhận từ form

    private String artistName; // Dùng để hiển thị tên
    private String genreName; // Dùng để hiển thị tên

    private String filePath; // Chỉ dùng để hiển thị hoặc khi không cần upload lại

    // Trường này chỉ dùng khi upload file mới, không map trực tiếp vào Entity
    private MultipartFile songFile;


}
