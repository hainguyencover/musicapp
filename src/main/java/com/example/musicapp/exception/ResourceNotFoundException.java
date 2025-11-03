package com.example.musicapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// @ResponseStatus(value = HttpStatus.NOT_FOUND):
// Tự động trả về HTTP status 404 Not Found khi exception này không được bắt bởi @ExceptionHandler
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L; // Recommended for Serializable classes

    public ResourceNotFoundException(String message) {
        super(message); // Gọi constructor của lớp cha (RuntimeException) với message lỗi
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
        // Example usage: throw new ResourceNotFoundException("Song", "id", id);
    }
}
