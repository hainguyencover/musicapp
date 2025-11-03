package com.example.musicapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GenreDTO {
    private Long id;

    @NotBlank(message = "Tên thể loại không được để trống")
    @Size(max = 50, message = "Tên thể loại tối đa 50 ký tự")
    private String name;

    // Constructor nếu cần
    public GenreDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
