package com.example.musicapp.dto;

import lombok.Data;

@Data
public class SongDTO {
    private Long id;
    private String title;
    private int duration;
    private String fileUrl;
    private String imageUrl;

    // Lồng DTO để tránh lỗi vòng lặp và chỉ hiển thị thông tin cần thiết
    private ArtistDTO artist;
    private GenreDTO genre;
}
