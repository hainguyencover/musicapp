package com.example.musicapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException; // Import thêm

@ControllerAdvice // Đánh dấu lớp này để xử lý exception toàn cục
public class GlobalExceptionHandler {
    // Xử lý ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // Đảm bảo trả về status 404
    public String handleResourceNotFoundException(ResourceNotFoundException ex, Model model) {
        System.err.println("ResourceNotFoundException caught: " + ex.getMessage()); // Log lỗi
        model.addAttribute("errorMessage", ex.getMessage()); // Gửi message lỗi tới view
        // Bạn có thể tạo một trang lỗi 404 riêng
        return "error/404"; // Trả về view templates/error/404.html
    }

    // Xử lý NoHandlerFoundException (khi không tìm thấy URL mapping)
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoHandlerFoundException(NoHandlerFoundException ex, Model model) {
        System.err.println("NoHandlerFoundException caught: " + ex.getRequestURL());
        model.addAttribute("errorMessage", "Xin lỗi, trang bạn tìm kiếm không tồn tại.");
        return "error/404"; // Dùng chung trang 404
    }

    // (Tùy chọn) Xử lý các lỗi chung khác (Exception.class là lớp cha của mọi exception)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // Status 500
    public String handleGenericException(Exception ex, Model model) {
        System.err.println("An unexpected error occurred: " + ex.getMessage());
        ex.printStackTrace(); // In stack trace ra console (quan trọng khi dev)
        model.addAttribute("errorMessage", "Đã có lỗi xảy ra. Vui lòng thử lại sau.");
        // Bạn có thể tạo một trang lỗi 500 riêng
        return "error/500"; // Trả về view templates/error/500.html
    }

    // (Tùy chọn) Xử lý lỗi lưu trữ file
    @ExceptionHandler(StorageException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleStorageException(StorageException ex, Model model) {
        System.err.println("StorageException caught: " + ex.getMessage());
        model.addAttribute("errorMessage", "Lỗi xử lý file: " + ex.getMessage());
        return "error/500"; // Hoặc một trang lỗi riêng cho file
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleStorageFileNotFoundException(StorageFileNotFoundException ex, Model model) {
        System.err.println("StorageFileNotFoundException caught: " + ex.getMessage());
        model.addAttribute("errorMessage", "Không tìm thấy file: " + ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(DeletionBlockedException.class)
    @ResponseStatus(HttpStatus.CONFLICT) // Status 409 Conflict
    public String handleDeletionBlockedException(DeletionBlockedException ex, Model model) {
        System.err.println("DeletionBlockedException caught: " + ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        // Có thể dùng chung trang 500 hoặc tạo trang lỗi riêng "error/conflict"
        return "error/500";
    }
}
