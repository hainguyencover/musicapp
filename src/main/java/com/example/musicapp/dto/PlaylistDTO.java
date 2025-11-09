package com.example.musicapp.dto;

import lombok.Data;
import java.util.List;

@Data
public class PlaylistDTO {
    private Long id;
    private String name;
    private Long accountId; // ID của người tạo
    private List<SongDTO> songs; // Danh sách các bài hát trong playlist
}
