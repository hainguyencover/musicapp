package com.example.musicapp.service.storage;

import org.springframework.core.io.Resource; // Dùng nếu có chức năng download/streaming
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream; // Dùng nếu có chức năng liệt kê file

public interface IFileStorageService {
    /**
     * Khởi tạo thư mục lưu trữ (nếu cần).
     */
    void init();

    /**
     * Lưu file được upload.
     *
     * @param file Đối tượng MultipartFile từ request.
     * @return Tên file đã được lưu (có thể là tên duy nhất được tạo ra).
     */
    String store(MultipartFile file);

    /**
     * Tải file dưới dạng Resource (dùng cho download/streaming).
     *
     * @param filename Tên file cần tải.
     * @return Đối tượng Resource đại diện cho file.
     */
    Resource loadAsResource(String filename);

    /**
     * Xóa file dựa trên tên file.
     *
     * @param filename Tên file cần xóa.
     */
    void delete(String filename);

    /**
     * Xóa tất cả các file trong thư mục lưu trữ (cẩn thận khi dùng!).
     */
    // void deleteAll(); // Nên cẩn thận khi implement chức năng này

    /**
     * Lấy đường dẫn đến file dựa trên tên file.
     *
     * @param filename Tên file.
     * @return Path đối tượng.
     */
    Path load(String filename);


    /**
     * Liệt kê tất cả các file trong thư mục lưu trữ.
     * @return Stream<Path>
     */
    // Stream<Path> loadAll(); // Ít dùng trong trường hợp này
}
