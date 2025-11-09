package com.example.musicapp.controller.admin;

import com.example.musicapp.dto.SongDTO;
import com.example.musicapp.entity.Song; // Vẫn cần nếu listSongs trả về Page<Song>
import com.example.musicapp.exception.DuplicateNameException; // Import nếu cần bắt lỗi trùng tên
import com.example.musicapp.exception.ResourceNotFoundException;
import com.example.musicapp.service.IArtistService;
import com.example.musicapp.service.IGenreService;
import com.example.musicapp.service.ISongService;
import com.example.musicapp.service.storage.IFileStorageService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

@Slf4j
@Controller
@RequestMapping("/admin/songs")
public class SongController {

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
                            @PageableDefault(page = 0, size = 5, sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
                            @RequestParam(required = false, value = "keyword") String keyword) {
        log.debug("Fetching song list for page: {}, size: {}, sort: {}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        Page<Song> songPage = songService.findAll(keyword, pageable);
        model.addAttribute("songPage", songPage);
        model.addAttribute("keyword", keyword);
        log.info("Displayed song list page {}", pageable.getPageNumber() + 1);
        return "admin/song/list";
    }

    // --- Hiển thị form thêm mới ---
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        log.info("Displaying create song form");
        model.addAttribute("songDTO", new SongDTO());
        model.addAttribute("artists", artistService.findAll());
        model.addAttribute("genres", genreService.findAll());
        return "admin/song/create";
    }

    // --- Xử lý thêm mới ---
    @PostMapping("/create")
    public String createSong(@Valid @ModelAttribute("songDTO") SongDTO songDTO,
                             BindingResult bindingResult,
                             @RequestParam("songFile") MultipartFile songFile,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        log.info("Attempting to create new song: {}", songDTO.getName());

        // --- Kiểm tra file ---
        boolean fileIsValid = true;
        if (songFile.isEmpty()) {
            bindingResult.rejectValue("songFile", "error.songFile.empty", "Vui lòng chọn file nhạc.");
            fileIsValid = false;
            log.warn("Validation failed: Song file is empty.");
        } else {
            try {
                long maxBytes = DataSize.parse(maxFileSize).toBytes();
                if (songFile.getSize() > maxBytes) {
                    bindingResult.rejectValue("songFile", "error.songFile.size", "Kích thước file không được vượt quá " + maxFileSize);
                    fileIsValid = false;
                    log.warn("Validation failed: File size {} exceeds limit {}", songFile.getSize(), maxFileSize);
                }
            } catch (IllegalArgumentException e) {
                bindingResult.rejectValue("songFile", "error.songFile.config", "Lỗi cấu hình kích thước file tối đa.");
                fileIsValid = false;
                log.error("Invalid max file size configuration: {}", maxFileSize, e);
            }

            String contentType = songFile.getContentType();
            if (contentType == null || !ALLOWED_AUDIO_TYPES.contains(contentType.toLowerCase())) {
                bindingResult.rejectValue("songFile", "error.songFile.type", "Chỉ chấp nhận các định dạng file audio (MP3, WAV, OGG, AAC).");
                fileIsValid = false;
                log.warn("Validation failed: Invalid file type '{}'", contentType);
            }
        }
        // --- Kết thúc kiểm tra file ---

        // Kiểm tra lỗi DTO + file
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors found when creating song '{}'. Returning to form.", songDTO.getName());
            model.addAttribute("artists", artistService.findAll());
            model.addAttribute("genres", genreService.findAll());
            return "admin/song/create";
        }

        // Gọi service để lưu
        try {
            Song savedSong = songService.save(songDTO, songFile);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm bài hát thành công!");
            log.info("Successfully created song '{}' with ID {}", savedSong.getName(), savedSong.getId());
            return "redirect:/admin/songs";
        } catch (ResourceNotFoundException e) {
            bindingResult.reject("error.generic", e.getMessage());
            log.warn("Failed to save song '{}' due to missing artist/genre: {}", songDTO.getName(), e.getMessage());
            model.addAttribute("artists", artistService.findAll());
            model.addAttribute("genres", genreService.findAll());
            return "admin/song/create";
        } catch (DuplicateNameException e) {
            bindingResult.rejectValue("name", "error.song.name.duplicate", e.getMessage());
            log.warn("Failed to save song due to duplicate name: {}", e.getMessage());
            model.addAttribute("artists", artistService.findAll());
            model.addAttribute("genres", genreService.findAll());
            return "admin/song/create";
        } catch (Exception e) {
            bindingResult.reject("error.generic", "Đã có lỗi xảy ra khi lưu bài hát: " + e.getMessage());
            log.error("Unexpected error saving song '{}': {}", songDTO.getName(), e.getMessage(), e); // Log stack trace
            model.addAttribute("artists", artistService.findAll());
            model.addAttribute("genres", genreService.findAll());
            return "admin/song/create";
        }
    }

    // --- Hiển thị form chỉnh sửa ---
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        log.info("Displaying edit form for song ID {}", id);
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
        return "admin/song/edit";
    }

    // --- Xử lý cập nhật ---
    @PostMapping("/update")
    public String updateSong(@Valid @ModelAttribute("songDTO") SongDTO songDTO,
                             BindingResult bindingResult,
                             @RequestParam(value = "songFile", required = false) MultipartFile songFile,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        log.info("Attempting to update song ID {}: {}", songDTO.getId(), songDTO.getName());

        // --- Kiểm tra file (chỉ nếu có file mới) ---
        boolean fileIsValid = true;
        if (songFile != null && !songFile.isEmpty()) {
            log.debug("New file uploaded for song ID {}", songDTO.getId());
            try {
                long maxBytes = DataSize.parse(maxFileSize).toBytes();
                if (songFile.getSize() > maxBytes) {
                    bindingResult.rejectValue("songFile", "error.songFile.size", "Kích thước file không được vượt quá " + maxFileSize);
                    fileIsValid = false;
                    log.warn("Update validation failed for song ID {}: File size {} exceeds limit {}", songDTO.getId(), songFile.getSize(), maxFileSize);
                }
            } catch (IllegalArgumentException e) {
                bindingResult.rejectValue("songFile", "error.songFile.config", "Lỗi cấu hình kích thước file tối đa.");
                fileIsValid = false;
                log.error("Invalid max file size configuration: {}", maxFileSize, e);
            }
            String contentType = songFile.getContentType();
            if (contentType == null || !ALLOWED_AUDIO_TYPES.contains(contentType.toLowerCase())) {
                bindingResult.rejectValue("songFile", "error.songFile.type", "Chỉ chấp nhận các định dạng file audio (MP3, WAV, OGG, AAC).");
                fileIsValid = false;
                log.warn("Update validation failed for song ID {}: Invalid file type '{}'", songDTO.getId(), contentType);
            }
        }
        // --- Kết thúc kiểm tra file ---

        // Kiểm tra lỗi DTO + file
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors found when updating song ID {}. Returning to edit form.", songDTO.getId());
            model.addAttribute("artists", artistService.findAll());
            model.addAttribute("genres", genreService.findAll());
            songService.findById(songDTO.getId()).ifPresent(song -> songDTO.setFilePath(song.getFilePath()));
            return "admin/song/edit";
        }

        // Gọi service để cập nhật
        try {
            Song updatedSong = songService.update(songDTO, songFile);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật bài hát thành công!");
            log.info("Successfully updated song '{}' with ID {}", updatedSong.getName(), updatedSong.getId());
            return "redirect:/admin/songs";
        } catch (ResourceNotFoundException e) {
            bindingResult.reject("error.generic", e.getMessage());
            log.warn("Failed to update song ID {} due to missing resource: {}", songDTO.getId(), e.getMessage());
            model.addAttribute("artists", artistService.findAll());
            model.addAttribute("genres", genreService.findAll());
            songService.findById(songDTO.getId()).ifPresent(song -> songDTO.setFilePath(song.getFilePath()));
            return "admin/song/edit";
        } catch (DuplicateNameException e) {
            bindingResult.rejectValue("name", "error.song.name.duplicate", e.getMessage());
            log.warn("Failed to update song ID {} due to duplicate name: {}", songDTO.getId(), e.getMessage());
            model.addAttribute("artists", artistService.findAll());
            model.addAttribute("genres", genreService.findAll());
            songService.findById(songDTO.getId()).ifPresent(song -> songDTO.setFilePath(song.getFilePath()));
            return "admin/song/edit";
        } catch (Exception e) {
            bindingResult.reject("error.generic", "Đã có lỗi xảy ra khi cập nhật bài hát: " + e.getMessage());
            log.error("Unexpected error updating song ID {}: {}", songDTO.getId(), e.getMessage(), e); // Log stack trace
            model.addAttribute("artists", artistService.findAll());
            model.addAttribute("genres", genreService.findAll());
            songService.findById(songDTO.getId()).ifPresent(song -> songDTO.setFilePath(song.getFilePath()));
            return "admin/song/edit";
        }
    }

    // --- Xử lý xóa ---
    @GetMapping("/delete/{id}")
    public String deleteSong(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("Attempting to delete song ID {}", id);
        try {
            // Lấy tên file trước khi xóa (để log) - Service sẽ xử lý xóa file thực tế
            Optional<Song> songOpt = songService.findById(id);
            String songName = songOpt.map(Song::getName).orElse("N/A");

            songService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa bài hát thành công!");
            log.info("Successfully deleted song ID {} (Name: {})", id, songName);
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            log.warn("Failed to delete song: Song ID {} not found.", id);
        } catch (Exception e) { // Bắt các lỗi khác (ví dụ: lỗi xóa file)
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa bài hát!");
            log.error("Error deleting song ID {}: {}", id, e.getMessage(), e); // Log stack trace
        }
        return "redirect:/admin/songs";
    }

}
