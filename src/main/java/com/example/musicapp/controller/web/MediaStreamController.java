package com.example.musicapp.controller.web;

import com.example.musicapp.exception.StorageFileNotFoundException;
import com.example.musicapp.service.storage.IFileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller này chịu trách nhiệm phục vụ (stream) các file media (nhạc, ảnh).
 * Các URL này được cấu hình permitAll() trong SecurityConfig.
 */
@Controller
public class MediaStreamController {

    private static final Logger logger = LoggerFactory.getLogger(MediaStreamController.class);
    private final IFileStorageService fileStorageService;

    public MediaStreamController(IFileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    /**
     * Phục vụ file nhạc (khớp với /songs/play/**)
     */
    @GetMapping("/songs/play/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveSongFile(@PathVariable String filename) {
        logger.debug("Request received to serve song: {}", filename);
        try {
            Resource file = fileStorageService.loadAsResource(filename);
            logger.info("Serving song: {}", filename);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                    // (Bạn có thể thêm Content-Type nếu biết chắc, ví dụ: .header(HttpHeaders.CONTENT_TYPE, "audio/mpeg") )
                    .body(file);
        } catch (StorageFileNotFoundException e) {
            logger.warn("Requested song file not found: {}", filename);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error serving song file {}: {}", filename, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Phục vụ file ảnh (khớp với /artists/photo/**)
     */
    @GetMapping("/artists/photo/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveArtistPhoto(@PathVariable String filename) {
        logger.debug("Request received to serve photo: {}", filename);
        try {
            Resource file = fileStorageService.loadAsResource(filename);
            logger.info("Serving photo: {}", filename);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                    // (Bạn có thể thêm Content-Type nếu biết chắc, ví dụ: .header(HttpHeaders.CONTENT_TYPE, "image/jpeg") )
                    .body(file);
        } catch (StorageFileNotFoundException e) {
            logger.warn("Requested photo file not found: {}", filename);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error serving photo file {}: {}", filename, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
