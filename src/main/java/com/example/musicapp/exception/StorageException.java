package com.example.musicapp.exception;

// --- Các lớp Exception tùy chỉnh (nên tạo file riêng) ---
public  class StorageException extends RuntimeException {
    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
