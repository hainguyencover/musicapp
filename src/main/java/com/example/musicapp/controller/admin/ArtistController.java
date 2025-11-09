package com.example.musicapp.controller.admin;

import com.example.musicapp.dto.ArtistDTO;
import com.example.musicapp.entity.Artist;
import com.example.musicapp.exception.DeletionBlockedException;
import com.example.musicapp.exception.DuplicateNameException;
import com.example.musicapp.exception.ResourceNotFoundException;
import com.example.musicapp.service.IArtistService;
import com.example.musicapp.service.storage.IFileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/admin/artists")
public class ArtistController {
    private final IArtistService artistService;
    private final IFileStorageService fileStorageService;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    public ArtistController(IArtistService artistService, IFileStorageService fileStorageService) {
        this.artistService = artistService;
        this.fileStorageService = fileStorageService;
    }

    // --- Hiển thị danh sách ---
//    @GetMapping
//    public String listArtists(Model model) {
//        List<ArtistDTO> artists = artistService.findAll().stream()
//                .map(artist -> new ArtistDTO(artist.getId(), artist.getName()))
//                .collect(Collectors.toList());
//        model.addAttribute("artists", artists);
//        return "artist/list"; // templates/artist/list.html
//    }

    @GetMapping
    public String listArtists(Model model,
                              @PageableDefault(page = 0, size = 5, sort = "name", direction = Sort.Direction.ASC)
                              Pageable pageable,
                              @RequestParam(value = "keyword", required = false) String keyword) {
        log.debug("Fetching artist list page {}", pageable.getPageNumber());
        Page<Artist> artistPageEntity = artistService.findAll(keyword, pageable);

        List<ArtistDTO> dtoList = artistPageEntity.getContent().stream()
                .map(artist -> {
                    ArtistDTO dto = new ArtistDTO();
                    dto.setId(artist.getId());
                    dto.setName(artist.getName());
                    dto.setAvatarPath(artist.getAvatarPath());
                    dto.setBio(artist.getBio());
                    return dto;
                })
                .collect(Collectors.toList());
        Page<ArtistDTO> artistPageDTO = new PageImpl<>(dtoList, pageable, artistPageEntity.getTotalElements());

        model.addAttribute("artistPage", artistPageDTO);
        model.addAttribute("keyword", keyword);
        return "admin/artist/list";
    }

    // --- Hiển thị form tạo mới ---
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        log.info("Displaying create artist form");
        model.addAttribute("artistDTO", new ArtistDTO());
        return "admin/artist/create"; // templates/artist/create.html
    }

    @PostMapping("/create")
    public String createArtist(@Valid @ModelAttribute("artistDTO") ArtistDTO artistDTO,
                               BindingResult bindingResult,
                               @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors in createArtist form.");
            return "admin/artist/create"; // Quay lại form nếu có lỗi DTO
        }
        try {
            Artist artist = new Artist();
            artist.setName(artistDTO.getName());
            artist.setBio(artistDTO.getBio());

            // Xử lý file ảnh đại diện nếu có
            if (avatarFile != null && !avatarFile.isEmpty()) {
                try {
                    long maxBytes = DataSize.parse(maxFileSize).toBytes();
                    if (avatarFile.getSize() > maxBytes) {
                        bindingResult.rejectValue("avatarFile", "error.avatarFile.size", "Kích thước file không được vượt quá " + maxFileSize);
                        return "admin/artist/create";
                    }
                } catch (IllegalArgumentException e) {
                    bindingResult.rejectValue("avatarFile", "error.avatarFile.config", "Lỗi cấu hình kích thước file tối đa.");
                    return "admin/artist/create";
                }
                String contentType = avatarFile.getContentType();
                if (contentType == null || !(contentType.equalsIgnoreCase("image/jpeg")
                        || contentType.equalsIgnoreCase("image/png")
                        || contentType.equalsIgnoreCase("image/webp")
                        || contentType.equalsIgnoreCase("image/gif"))) {
                    bindingResult.rejectValue("avatarFile", "error.avatarFile.type", "Chỉ chấp nhận ảnh JPEG/PNG/WEBP/GIF.");
                    return "admin/artist/create";
                }
                String storedAvatar = fileStorageService.store(avatarFile);
                artist.setAvatarPath(storedAvatar);
            }
            artistService.save(artist);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm nghệ sĩ thành công!");
            log.info("Artist created successfully: {}", artist.getName());
            return "redirect:/admin/artists";
        } catch (DuplicateNameException e) { // SỬA: Bắt DuplicateNameException
            log.warn("Failed to create artist due to duplicate name: {}", e.getMessage());
            bindingResult.rejectValue("name", "error.artist.name.duplicate", e.getMessage()); // Thêm lỗi vào field 'name'
            return "admin/artist/create"; // Quay lại form với thông báo lỗi
        } catch (Exception e) {
            log.error("Error saving artist: {}", e.getMessage(), e);
            bindingResult.reject("error.generic", "Đã có lỗi xảy ra khi lưu nghệ sĩ.");
            return "admin/artist/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        log.info("Displaying edit form for artist ID {}", id);
        Artist artist = artistService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artist", "id", id));
        ArtistDTO artistDTO = new ArtistDTO(artist.getId(), artist.getName());
        artistDTO.setBio(artist.getBio());
        artistDTO.setAvatarPath(artist.getAvatarPath());
        model.addAttribute("artistDTO", artistDTO);
        return "admin/artist/edit";
    }

    @PostMapping("/update")
    public String updateArtist(@Valid @ModelAttribute("artistDTO") ArtistDTO artistDTO,
                               BindingResult bindingResult,
                               @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors in updateArtist form for ID {}.", artistDTO.getId());
            return "admin/artist/edit";
        }
        try {
            Artist existingArtist = artistService.findById(artistDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Artist", "id", artistDTO.getId()));
            existingArtist.setName(artistDTO.getName());
            existingArtist.setBio(artistDTO.getBio());

            if (avatarFile != null && !avatarFile.isEmpty()) {
                try {
                    long maxBytes = DataSize.parse(maxFileSize).toBytes();
                    if (avatarFile.getSize() > maxBytes) {
                        bindingResult.rejectValue("avatarFile", "error.avatarFile.size", "Kích thước file không được vượt quá " + maxFileSize);
                        return "admin/artist/edit";
                    }
                } catch (IllegalArgumentException e) {
                    bindingResult.rejectValue("avatarFile", "error.avatarFile.config", "Lỗi cấu hình kích thước file tối đa.");
                    return "admin/artist/edit";
                }
                String contentType = avatarFile.getContentType();
                if (contentType == null || !(contentType.equalsIgnoreCase("image/jpeg")
                        || contentType.equalsIgnoreCase("image/png")
                        || contentType.equalsIgnoreCase("image/webp")
                        || contentType.equalsIgnoreCase("image/gif"))) {
                    bindingResult.rejectValue("avatarFile", "error.avatarFile.type", "Chỉ chấp nhận ảnh JPEG/PNG/WEBP/GIF.");
                    return "admin/artist/edit";
                }
                // Xóa file cũ nếu có
                if (existingArtist.getAvatarPath() != null && !existingArtist.getAvatarPath().isEmpty()) {
                    fileStorageService.delete(existingArtist.getAvatarPath());
                }
                String storedAvatar = fileStorageService.store(avatarFile);
                existingArtist.setAvatarPath(storedAvatar);
            }
            artistService.save(existingArtist);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật nghệ sĩ thành công!");
            log.info("Artist updated successfully: {}", existingArtist.getName());
            return "redirect:/admin/artists";
        } catch (DuplicateNameException e) { // SỬA: Bắt DuplicateNameException
            log.warn("Failed to update artist ID {} due to duplicate name: {}", artistDTO.getId(), e.getMessage());
            bindingResult.rejectValue("name", "error.artist.name.duplicate", e.getMessage());
            return "admin/artist/edit";
        } catch (Exception e) {
            log.error("Error updating artist ID {}: {}", artistDTO.getId(), e.getMessage(), e);
            bindingResult.reject("error.generic", "Đã có lỗi xảy ra khi cập nhật nghệ sĩ.");
            return "admin/artist/edit";
        }
    }

    // --- Xử lý xóa ---
    @GetMapping("/delete/{id}")
    public String deleteArtist(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("Attempting to delete artist ID {}", id);
        try {
            artistService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa nghệ sĩ thành công!");
            log.info("Artist deleted successfully, ID {}", id);
        } catch (ResourceNotFoundException e) {
            log.warn("Failed to delete artist: ID {} not found.", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy nghệ sĩ để xóa!");
        } catch (DeletionBlockedException | DataIntegrityViolationException e) {
            log.warn("Failed to delete artist ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            log.error("Error deleting artist ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Đã có lỗi xảy ra khi xóa nghệ sĩ.");
        }
        return "redirect:/admin/artists";
    }


}
