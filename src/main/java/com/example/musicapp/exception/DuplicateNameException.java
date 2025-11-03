package com.example.musicapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception được ném ra khi cố gắng tạo một tài nguyên (ví dụ: Artist, Genre)
 * với tên đã tồn tại trong hệ thống.
 * Mặc định trả về HTTP Status 409 Conflict.
 */
@ResponseStatus(value = HttpStatus.CONFLICT) // 409 Conflict
public class DuplicateNameException extends RuntimeException {

    private static final long serialVersionUID = 1L; // Recommended for Serializable classes

    public DuplicateNameException(String message) {
        super(message);
    }
}
