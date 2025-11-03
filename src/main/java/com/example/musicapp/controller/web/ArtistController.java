package com.example.musicapp.controller.web;

import com.example.musicapp.dto.ArtistDTO;
import com.example.musicapp.entity.Artist;
import com.example.musicapp.exception.DeletionBlockedException;
import com.example.musicapp.exception.ResourceNotFoundException;
import com.example.musicapp.service.IArtistService;
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

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/artists")
public class ArtistController {
    private final IArtistService artistService;

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
        Page<Artist> artistPageEntity = artistService.findAll(pageable);

        // Map Page<Artist> sang Page<ArtistDTO>
        List<ArtistDTO> dtoList = artistPageEntity.getContent().stream()
                .map(artist -> new ArtistDTO(artist.getId(), artist.getName()))
                .collect(Collectors.toList());
        Page<ArtistDTO> artistPageDTO = new PageImpl<>(dtoList, pageable, artistPageEntity.getTotalElements());

        model.addAttribute("artistPage", artistPageDTO); // Gửi Page DTO
        return "artist/list";
    }

    // --- Hiển thị form tạo mới ---
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("artistDTO", new ArtistDTO());
        return "artist/create"; // templates/artist/create.html
    }

    // --- Xử lý tạo mới ---
    @PostMapping("/create")
    public String createArtist(@Valid @ModelAttribute("artistDTO") ArtistDTO artistDTO,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "artist/create"; // Quay lại form nếu có lỗi
        }
        try {
            Artist artist = new Artist();
            artist.setName(artistDTO.getName());
            artistService.save(artist);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm nghệ sĩ thành công!");
            return "redirect:/artists";
        } catch (Exception e) { // Bắt lỗi chung (ví dụ: trùng tên nếu có constraint DB)
            bindingResult.reject("error.generic", "Đã có lỗi xảy ra khi lưu nghệ sĩ.");
            System.err.println("Error saving artist: " + e.getMessage());
            return "artist/create";
        }
    }

    // --- Hiển thị form sửa ---
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Artist artist = artistService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artist", "id", id));
        ArtistDTO artistDTO = new ArtistDTO(artist.getId(), artist.getName());
        model.addAttribute("artistDTO", artistDTO);
        return "artist/edit"; // templates/artist/edit.html
    }

    // --- Xử lý cập nhật ---
    @PostMapping("/update")
    public String updateArtist(@Valid @ModelAttribute("artistDTO") ArtistDTO artistDTO,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "artist/edit";
        }
        try {
            Artist existingArtist = artistService.findById(artistDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Artist", "id", artistDTO.getId()));
            existingArtist.setName(artistDTO.getName());
            artistService.save(existingArtist);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật nghệ sĩ thành công!");
            return "redirect:/artists";
        } catch (Exception e) {
            bindingResult.reject("error.generic", "Đã có lỗi xảy ra khi cập nhật nghệ sĩ.");
            System.err.println("Error updating artist: " + e.getMessage());
            return "artist/edit";
        }
    }

    // --- Xử lý xóa ---
    @GetMapping("/delete/{id}")
    public String deleteArtist(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            artistService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa nghệ sĩ thành công!");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy nghệ sĩ để xóa!");
        } catch (DeletionBlockedException | DataIntegrityViolationException e) { // Bắt cả 2 loại exception
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage()); // Lấy message từ service
            System.err.println("Error deleting artist: " + e.getMessage());
        } catch (Exception e) { // Bắt lỗi không mong muốn khác
            redirectAttributes.addFlashAttribute("errorMessage", "Đã có lỗi xảy ra khi xóa nghệ sĩ.");
            System.err.println("Error deleting artist: " + e.getMessage());
        }
        return "redirect:/artists";
    }
}
