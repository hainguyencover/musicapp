package com.example.musicapp.service.storage;

import com.example.musicapp.exception.StorageException; // Import exception đã tách file
import com.example.musicapp.exception.StorageFileNotFoundException; // Import exception đã tách file
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger; // Import SLF4j Logger
import org.slf4j.LoggerFactory; // Import SLF4j LoggerFactory
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class LocalFileStorageService implements IFileStorageService {

    // Khai báo Logger
    private static final Logger logger = LoggerFactory.getLogger(LocalFileStorageService.class);

    @Value("${file.upload-dir}")
    private String uploadDir;

    private Path rootLocation;

    @Override
    @PostConstruct
    public void init() {
        try {
            rootLocation = Paths.get(uploadDir);
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
                // Ghi log INFO thay vì System.out
                logger.info("Đã tạo thư mục upload: {}", rootLocation.toAbsolutePath());
            } else {
                // Ghi log DEBUG vì thông tin này ít quan trọng hơn
                logger.debug("Thư mục upload đã tồn tại: {}", rootLocation.toAbsolutePath());
            }
        } catch (IOException e) {
            // Ghi log ERROR khi không khởi tạo được
            logger.error("Không thể khởi tạo thư mục lưu trữ tại {}", uploadDir, e);
            throw new StorageException("Không thể khởi tạo thư mục lưu trữ", e);
        } catch (InvalidPathException ipe) { // <-- Bắt lỗi này để xem rõ hơn
            logger.error("Đường dẫn upload không hợp lệ: '{}'", uploadDir, ipe);
            throw new StorageException("Đường dẫn upload không hợp lệ: " + uploadDir, ipe);
        }
    }

    @Override
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            logger.warn("Cố gắng lưu trữ file rỗng."); // Dùng WARN
            throw new StorageException("Không thể lưu trữ file rỗng.");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = StringUtils.getFilenameExtension(originalFilename);
        String storedFilename = UUID.randomUUID().toString() + "." + extension;

        try {
            if (originalFilename.contains("..")) {
                logger.error("Lỗi bảo mật: Không thể lưu file '{}' với đường dẫn tương đối.", originalFilename); // Dùng ERROR
                throw new StorageException("Không thể lưu file với đường dẫn tương đối " + originalFilename);
            }

            Path destinationFile = this.rootLocation.resolve(Paths.get(storedFilename)).normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                logger.error("Lỗi bảo mật: Cố gắng lưu file '{}' ra ngoài thư mục upload.", originalFilename); // Dùng ERROR
                throw new StorageException("Không thể lưu file ra ngoài thư mục hiện tại.");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
                logger.info("Đã lưu file '{}' thành '{}'", originalFilename, storedFilename); // Dùng INFO
            }

            return storedFilename;

        } catch (IOException e) {
            // Dùng ERROR khi có lỗi IO
            logger.error("Không thể lưu file '{}'. Lý do: {}", originalFilename, e.getMessage(), e);
            throw new StorageException("Không thể lưu file " + originalFilename, e);
        }
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        logger.debug("Đang tải file dưới dạng resource: {}", filename); // Dùng DEBUG
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                logger.info("Đã tải thành công resource cho file: {}", filename); // Dùng INFO
                return resource;
            } else {
                logger.warn("Không thể đọc file: {}", filename); // Dùng WARN
                throw new StorageFileNotFoundException("Không thể đọc file: " + filename);
            }
        } catch (MalformedURLException e) {
            logger.error("Lỗi URL không hợp lệ khi tải file '{}': {}", filename, e.getMessage(), e); // Dùng ERROR
            throw new StorageFileNotFoundException("Không thể đọc file: " + filename, e);
        } catch (StorageFileNotFoundException e) { // Bắt lại để log trước khi ném đi
            logger.warn("Không tìm thấy file resource: {}", filename); // Dùng WARN
            throw e; // Ném lại exception
        }
    }

    @Override
    public void delete(String filename) {
        if (filename == null || filename.isBlank()) {
            logger.warn("Cố gắng xóa file với tên rỗng hoặc null."); // Dùng WARN
            return;
        }
        try {
            Path fileToDelete = load(filename).normalize().toAbsolutePath();

            if (!fileToDelete.getParent().equals(this.rootLocation.toAbsolutePath())) {
                logger.warn("Cố gắng xóa file '{}' nằm ngoài thư mục upload.", filename); // Dùng WARN
                return;
            }

            boolean deleted = Files.deleteIfExists(fileToDelete);
            if (!deleted) {
                logger.info("Không tìm thấy file để xóa: {}", filename); // Dùng INFO
            } else {
                logger.info("Đã xóa file: {}", filename); // Dùng INFO
            }
        } catch (IOException e) {
            // Dùng ERROR khi xóa file thất bại
            logger.error("Không thể xóa file {}. Lý do: {}", filename, e.getMessage(), e);
            // Cân nhắc có nên ném StorageException ở đây không, tùy thuộc logic gọi hàm delete
            // throw new StorageException("Failed to delete file " + filename, e);
        }
    }

    // Các phương thức loadAll(), deleteAll() nếu có cũng cần thêm logging tương tự
}
