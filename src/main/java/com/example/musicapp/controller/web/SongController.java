package com.example.musicapp.controller.web;

import com.example.musicapp.dto.SongDTO;
import com.example.musicapp.entity.Song; // Vẫn cần nếu listSongs trả về Page<Song>
import com.example.musicapp.exception.DuplicateNameException; // Import nếu cần bắt lỗi trùng tên
import com.example.musicapp.exception.ResourceNotFoundException;
import com.example.musicapp.exception.StorageFileNotFoundException; // Import exception file
import com.example.musicapp.service.IArtistService;
import com.example.musicapp.service.IGenreService;
import com.example.musicapp.service.ISongService;
import com.example.musicapp.service.storage.IFileStorageService;
import jakarta.validation.Valid;
import org.slf4j.Logger; // Import SLF4j Logger
import org.slf4j.LoggerFactory; // Import SLF4j LoggerFactory
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/songs")
public class SongController {

    // Khai báo Logger cho class này
    private static final Logger logger = LoggerFactory.getLogger(SongController.class);

    private final ISongService songService;
    private final IArtistService artistService;
    private final IGenreService genreService;
    private final IFileStorageService fileStorageService;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    private static final List<String> ALLOWED_AUDIO_TYPES = Arrays.asList(
            "audio/mpeg", "audio/wav", "audio/ogg", "audio/aac"
    );

    public SongController(ISongService songService, IArtistService artistService,
                          IGenreService genreService, IFileStorageService fileStorageService) {
        this.songService = songService;
        this.artistService = artistService;
        this.genreService = genreService;
        this.fileStorageService = fileStorageService;
    }

    // --- Hiển thị danh sách ---
    @GetMapping
    public String listSongs(Model model,
                            @PageableDefault(page = 0, size = 5, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        logger.debug("Fetching song list for page: {}, size: {}, sort: {}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        Page<Song> songPage = songService.findAll(pageable);
        model.addAttribute("songPage", songPage);
        logger.info("Displayed song list page {}", pageable.getPageNumber() + 1);
        return "song/list";
    }

    // --- Hiển thị form thêm mới ---
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        logger.info("Displaying create song form");
        model.addAttribute("songDTO", new SongDTO());
        model.addAttribute("artists", artistService.findAll());
        model.addAttribute("genres", genreService.findAll());
        return "song/create";
    }

    // --- Xử lý thêm mới ---
    @PostMapping("/create")
    public String createSong(@Valid @ModelAttribute("songDTO") SongDTO songDTO,
                             BindingResult bindingResult,
                             @RequestParam("songFile") MultipartFile songFile,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        logger.info("Attempting to create new song: {}", songDTO.getName());

        // --- Kiểm tra file ---
        boolean fileIsValid = true;
        if (songFile.isEmpty()) {
            bindingResult.rejectValue("songFile", "error.songFile.empty", "Vui lòng chọn file nhạc.");
            fileIsValid = false;
            logger.warn("Validation failed: Song file is empty.");
        } else {
            try {
                long maxBytes = DataSize.parse(maxFileSize).toBytes();
                if (songFile.getSize() > maxBytes) {
                    bindingResult.rejectValue("songFile", "error.songFile.size", "Kích thước file không được vượt quá " + maxFileSize);
                    fileIsValid = false;
                    logger.warn("Validation failed: File size {} exceeds limit {}", songFile.getSize(), maxFileSize);
                }
            } catch (IllegalArgumentException e) {
                bindingResult.rejectValue("songFile", "error.songFile.config", "Lỗi cấu hình kích thước file tối đa.");
                fileIsValid = false;
                logger.error("Invalid max file size configuration: {}", maxFileSize, e);
            }

            String contentType = songFile.getContentType();
            if (contentType == null || !ALLOWED_AUDIO_TYPES.contains(contentType.toLowerCase())) {
                bindingResult.rejectValue("songFile", "error.songFile.type", "Chỉ chấp nhận các định dạng file audio (MP3, WAV, OGG, AAC).");
                fileIsValid = false;
                logger.warn("Validation failed: Invalid file type '{}'", contentType);
            }
        }
        // --- Kết thúc kiểm tra file ---

        // Kiểm tra lỗi DTO + file
        if (bindingResult.hasErrors()) {
            logger.warn("Validation errors found when creating song '{}'. Returning to form.", songDTO.getName());
            model.addAttribute("artists", artistService.findAll());
            model.addAttribute("genres", genreService.findAll());
            return "song/create";
        }

        // Gọi service để lưu
        try {
            Song savedSong = songService.save(songDTO, songFile);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm bài hát thành công!");
            logger.info("Successfully created song '{}' with ID {}", savedSong.getName(), savedSong.getId());
            return "redirect:/songs";
        } catch (ResourceNotFoundException e) {
            bindingResult.reject("error.generic", e.getMessage());
            logger.warn("Failed to save song '{}' due to missing artist/genre: {}", songDTO.getName(), e.getMessage());
            model.addAttribute("artists", artistService.findAll());
            model.addAttribute("genres", genreService.findAll());
            return "song/create";
        } catch (DuplicateNameException e) {
            bindingResult.rejectValue("name", "error.song.name.duplicate", e.getMessage());
            logger.warn("Failed to save song due to duplicate name: {}", e.getMessage());
            model.addAttribute("artists", artistService.findAll());
            model.addAttribute("genres", genreService.findAll());
            return "song/create";
        } catch (Exception e) {
            bindingResult.reject("error.generic", "Đã có lỗi xảy ra khi lưu bài hát: " + e.getMessage());
            logger.error("Unexpected error saving song '{}': {}", songDTO.getName(), e.getMessage(), e); // Log stack trace
            model.addAttribute("artists", artistService.findAll());
            model.addAttribute("genres", genreService.findAll());
            return "song/create";
        }
    }

    // --- Hiển thị form chỉnh sửa ---
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        logger.info("Displaying edit form for song ID {}", id);
        Song song = songService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Song", "id", id));

        SongDTO songDTO = new SongDTO();
        songDTO.setId(song.getId());
        songDTO.setName(song.getName());
        songDTO.setFilePath(song.getFilePath());
        if (song.getArtist() != null) songDTO.setArtistId(song.getArtist().getId());
        if (song.getGenre() != null) songDTO.setGenreId(song.getGenre().getId());

        model.addAttribute("songDTO", songDTO);
        model.addAttribute("artists", artistService.findAll());
        model.addAttribute("genres", genreService.findAll());
        return "song/edit";
    }

    // --- Xử lý cập nhật ---
    @PostMapping("/update")
    public String updateSong(@Valid @ModelAttribute("songDTO") SongDTO songDTO,
                             BindingResult bindingResult,
                             @RequestParam(value = "songFile", required = false) MultipartFile songFile,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        logger.info("Attempting to update song ID {}: {}", songDTO.getId(), songDTO.getName());

        // --- Kiểm tra file (chỉ nếu có file mới) ---
        boolean fileIsValid = true;
        if (songFile != null && !songFile.isEmpty()) {
            logger.debug("New file uploaded for song ID {}", songDTO.getId());
            try {
                long maxBytes = DataSize.parse(maxFileSize).toBytes();
                if (songFile.getSize() > maxBytes) {
                    bindingResult.rejectValue("songFile", "error.songFile.size", "Kích thước file không được vượt quá " + maxFileSize);
                    fileIsValid = false;
                    logger.warn("Update validation failed for song ID {}: File size {} exceeds limit {}", songDTO.getId(), songFile.getSize(), maxFileSize);
                }
            } catch (IllegalArgumentException e) {
                bindingResult.rejectValue("songFile", "error.songFile.config", "Lỗi cấu hình kích thước file tối đa.");
                fileIsValid = false;
                logger.error("Invalid max file size configuration: {}", maxFileSize, e);
            }
            String contentType = songFile.getContentType();
            if (contentType == null || !ALLOWED_AUDIO_TYPES.contains(contentType.toLowerCase())) {
                bindingResult.rejectValue("songFile", "error.songFile.type", "Chỉ chấp nhận các định dạng file audio (MP3, WAV, OGG, AAC).");
                fileIsValid = false;
                logger.warn("Update validation failed for song ID {}: Invalid file type '{}'", songDTO.getId(), contentType);
            }
        }
        // --- Kết thúc kiểm tra file ---

        // Kiểm tra lỗi DTO + file
        if (bindingResult.hasErrors()) {
            logger.warn("Validation errors found when updating song ID {}. Returning to edit form.", songDTO.getId());
            model.addAttribute("artists", artistService.findAll());
            model.addAttribute("genres", genreService.findAll());
            songService.findById(songDTO.getId()).ifPresent(song -> songDTO.setFilePath(song.getFilePath()));
            return "song/edit";
        }

        // Gọi service để cập nhật
        try {
            Song updatedSong = songService.update(songDTO, songFile);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật bài hát thành công!");
            logger.info("Successfully updated song '{}' with ID {}", updatedSong.getName(), updatedSong.getId());
            return "redirect:/songs";
        } catch (ResourceNotFoundException e) {
            bindingResult.reject("error.generic", e.getMessage());
            logger.warn("Failed to update song ID {} due to missing resource: {}", songDTO.getId(), e.getMessage());
            model.addAttribute("artists", artistService.findAll());
            model.addAttribute("genres", genreService.findAll());
            songService.findById(songDTO.getId()).ifPresent(song -> songDTO.setFilePath(song.getFilePath()));
            return "song/edit";
        } catch (DuplicateNameException e) {
            bindingResult.rejectValue("name", "error.song.name.duplicate", e.getMessage());
            logger.warn("Failed to update song ID {} due to duplicate name: {}", songDTO.getId(), e.getMessage());
            model.addAttribute("artists", artistService.findAll());
            model.addAttribute("genres", genreService.findAll());
            songService.findById(songDTO.getId()).ifPresent(song -> songDTO.setFilePath(song.getFilePath()));
            return "song/edit";
        } catch (Exception e) {
            bindingResult.reject("error.generic", "Đã có lỗi xảy ra khi cập nhật bài hát: " + e.getMessage());
            logger.error("Unexpected error updating song ID {}: {}", songDTO.getId(), e.getMessage(), e); // Log stack trace
            model.addAttribute("artists", artistService.findAll());
            model.addAttribute("genres", genreService.findAll());
            songService.findById(songDTO.getId()).ifPresent(song -> songDTO.setFilePath(song.getFilePath()));
            return "song/edit";
        }
    }

    // --- Xử lý xóa ---
    @GetMapping("/delete/{id}")
    public String deleteSong(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Attempting to delete song ID {}", id);
        try {
            // Lấy tên file trước khi xóa (để log) - Service sẽ xử lý xóa file thực tế
            Optional<Song> songOpt = songService.findById(id);
            String songName = songOpt.map(Song::getName).orElse("N/A");

            songService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa bài hát thành công!");
            logger.info("Successfully deleted song ID {} (Name: {})", id, songName);
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            logger.warn("Failed to delete song: Song ID {} not found.", id);
        } catch (Exception e) { // Bắt các lỗi khác (ví dụ: lỗi xóa file)
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa bài hát!");
            logger.error("Error deleting song ID {}: {}", id, e.getMessage(), e); // Log stack trace
        }
        return "redirect:/songs";
    }

    // --- Endpoint để phục vụ file nhạc ---
    @GetMapping("/play/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        logger.debug("Request received to serve file: {}", filename);
        try {
            Resource file = fileStorageService.loadAsResource(filename);
            logger.info("Serving file: {}", filename);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename=\"" + file.getFilename() + "\"").body(file);
        } catch (StorageFileNotFoundException e) {
            logger.warn("Requested file not found: {}", filename);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error serving file {}: {}", filename, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
