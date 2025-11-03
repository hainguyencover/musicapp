package com.example.musicapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ArtistDTO {
    private Long id;

    @NotBlank(message = "Tên nghệ sĩ không được để trống")
    @Size(max = 100, message = "Tên nghệ sĩ tối đa 100 ký tự")
    private String name;

    // Constructor nếu cần
    public ArtistDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
