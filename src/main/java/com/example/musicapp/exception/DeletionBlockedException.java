package com.example.musicapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Trả về lỗi 409 Conflict nếu exception này không được bắt ở ControllerAdvice
@ResponseStatus(value = HttpStatus.CONFLICT)
public class DeletionBlockedException extends RuntimeException {
    public DeletionBlockedException(String message) {
        super(message);
    }
}
