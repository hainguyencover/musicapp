package com.example.musicapp.controller.web;

import com.example.musicapp.dto.ArtistDTO;
import com.example.musicapp.entity.Artist;
import com.example.musicapp.exception.DeletionBlockedException;
import com.example.musicapp.exception.DuplicateNameException;
import com.example.musicapp.exception.ResourceNotFoundException;
import com.example.musicapp.service.IArtistService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/artists")
public class ArtistController {
    private final IArtistService artistService;

    // THÊM LOGGER
    private static final Logger logger = LoggerFactory.getLogger(ArtistController.class);

    public ArtistController(IArtistService artistService) {
        this.artistService = artistService;
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
                              @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        logger.debug("Fetching artist list page {}", pageable.getPageNumber());
        Page<Artist> artistPageEntity = artistService.findAll(pageable);

        List<ArtistDTO> dtoList = artistPageEntity.getContent().stream()
                .map(artist -> new ArtistDTO(artist.getId(), artist.getName()))
                .collect(Collectors.toList());
        Page<ArtistDTO> artistPageDTO = new PageImpl<>(dtoList, pageable, artistPageEntity.getTotalElements());

        model.addAttribute("artistPage", artistPageDTO);
        return "artist/list";
    }

    // --- Hiển thị form tạo mới ---
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        logger.info("Displaying create artist form");
        model.addAttribute("artistDTO", new ArtistDTO());
        return "artist/create"; // templates/artist/create.html
    }

    @PostMapping("/create")
    public String createArtist(@Valid @ModelAttribute("artistDTO") ArtistDTO artistDTO,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            logger.warn("Validation errors in createArtist form.");
            return "artist/create"; // Quay lại form nếu có lỗi DTO
        }
        try {
            Artist artist = new Artist();
            artist.setName(artistDTO.getName());
            artistService.save(artist);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm nghệ sĩ thành công!");
            logger.info("Artist created successfully: {}", artist.getName());
            return "redirect:/artists";
        } catch (DuplicateNameException e) { // SỬA: Bắt DuplicateNameException
            logger.warn("Failed to create artist due to duplicate name: {}", e.getMessage());
            bindingResult.rejectValue("name", "error.artist.name.duplicate", e.getMessage()); // Thêm lỗi vào field 'name'
            return "artist/create"; // Quay lại form với thông báo lỗi
        } catch (Exception e) {
            logger.error("Error saving artist: {}", e.getMessage(), e);
            bindingResult.reject("error.generic", "Đã có lỗi xảy ra khi lưu nghệ sĩ.");
            return "artist/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        logger.info("Displaying edit form for artist ID {}", id);
        Artist artist = artistService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artist", "id", id));
        ArtistDTO artistDTO = new ArtistDTO(artist.getId(), artist.getName());
        model.addAttribute("artistDTO", artistDTO);
        return "artist/edit";
    }

    @PostMapping("/update")
    public String updateArtist(@Valid @ModelAttribute("artistDTO") ArtistDTO artistDTO,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            logger.warn("Validation errors in updateArtist form for ID {}.", artistDTO.getId());
            return "artist/edit";
        }
        try {
            Artist existingArtist = artistService.findById(artistDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Artist", "id", artistDTO.getId()));
            existingArtist.setName(artistDTO.getName());
            artistService.save(existingArtist);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật nghệ sĩ thành công!");
            logger.info("Artist updated successfully: {}", existingArtist.getName());
            return "redirect:/artists";
        } catch (DuplicateNameException e) { // SỬA: Bắt DuplicateNameException
            logger.warn("Failed to update artist ID {} due to duplicate name: {}", artistDTO.getId(), e.getMessage());
            bindingResult.rejectValue("name", "error.artist.name.duplicate", e.getMessage());
            return "artist/edit";
        } catch (Exception e) {
            logger.error("Error updating artist ID {}: {}", artistDTO.getId(), e.getMessage(), e);
            bindingResult.reject("error.generic", "Đã có lỗi xảy ra khi cập nhật nghệ sĩ.");
            return "artist/edit";
        }
    }

    // --- Xử lý xóa ---
    @GetMapping("/delete/{id}")
    public String deleteArtist(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Attempting to delete artist ID {}", id);
        try {
            artistService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa nghệ sĩ thành công!");
            logger.info("Artist deleted successfully, ID {}", id);
        } catch (ResourceNotFoundException e) {
            logger.warn("Failed to delete artist: ID {} not found.", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy nghệ sĩ để xóa!");
        } catch (DeletionBlockedException | DataIntegrityViolationException e) {
            logger.warn("Failed to delete artist ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            logger.error("Error deleting artist ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Đã có lỗi xảy ra khi xóa nghệ sĩ.");
        }
        return "redirect:/artists";
    }
}
