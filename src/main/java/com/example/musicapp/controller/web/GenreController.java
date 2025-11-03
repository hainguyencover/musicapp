package com.example.musicapp.controller.web;

import com.example.musicapp.dto.GenreDTO;
import com.example.musicapp.entity.Genre;
import com.example.musicapp.exception.DeletionBlockedException;
import com.example.musicapp.exception.ResourceNotFoundException;
import com.example.musicapp.service.IGenreService;
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
@RequestMapping("/genres")
public class GenreController {
    private final IGenreService genreService;

    public GenreController(IGenreService genreService) {
        this.genreService = genreService;
    }

    // --- Hiển thị danh sách ---
//    @GetMapping
//    public String listGenres(Model model) {
//        List<GenreDTO> genres = genreService.findAll().stream()
//                .map(genre -> new GenreDTO(genre.getId(), genre.getName()))
//                .collect(Collectors.toList());
//        model.addAttribute("genres", genres);
//        return "genre/list"; // templates/genre/list.html
//    }

    @GetMapping
    public String listGenres(Model model,
                             @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<Genre> genrePageEntity = genreService.findAll(pageable);

        // Map Page<Genre> sang Page<GenreDTO>
        List<GenreDTO> dtoList = genrePageEntity.getContent().stream()
                .map(genre -> new GenreDTO(genre.getId(), genre.getName()))
                .collect(Collectors.toList());
        Page<GenreDTO> genrePageDTO = new PageImpl<>(dtoList, pageable, genrePageEntity.getTotalElements());

        model.addAttribute("genrePage", genrePageDTO); // Gửi Page DTO
        return "genre/list";
    }

    // --- Hiển thị form tạo mới ---
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("genreDTO", new GenreDTO());
        return "genre/create"; // templates/genre/create.html
    }

    // --- Xử lý tạo mới ---
    @PostMapping("/create")
    public String createGenre(@Valid @ModelAttribute("genreDTO") GenreDTO genreDTO,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "genre/create";
        }
        try {
            Genre genre = new Genre();
            genre.setName(genreDTO.getName());
            genreService.save(genre);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm thể loại thành công!");
            return "redirect:/genres";
        } catch (Exception e) {
            bindingResult.reject("error.generic", "Đã có lỗi xảy ra khi lưu thể loại.");
            System.err.println("Error saving genre: " + e.getMessage());
            return "genre/create";
        }
    }

    // --- Hiển thị form sửa ---
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Genre genre = genreService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genre", "id", id));
        GenreDTO genreDTO = new GenreDTO(genre.getId(), genre.getName());
        model.addAttribute("genreDTO", genreDTO);
        return "genre/edit"; // templates/genre/edit.html
    }

    // --- Xử lý cập nhật ---
    @PostMapping("/update")
    public String updateGenre(@Valid @ModelAttribute("genreDTO") GenreDTO genreDTO,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "genre/edit";
        }
        try {
            Genre existingGenre = genreService.findById(genreDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Genre", "id", genreDTO.getId()));
            existingGenre.setName(genreDTO.getName());
            genreService.save(existingGenre);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thể loại thành công!");
            return "redirect:/genres";
        } catch (Exception e) {
            bindingResult.reject("error.generic", "Đã có lỗi xảy ra khi cập nhật thể loại.");
            System.err.println("Error updating genre: " + e.getMessage());
            return "genre/edit";
        }
    }

    // --- Xử lý xóa ---
    @GetMapping("/delete/{id}")
    public String deleteGenre(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            genreService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa thể loại thành công!");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thể loại để xóa!");
        } catch (DeletionBlockedException | DataIntegrityViolationException e) { // Bắt cả 2 loại exception
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage()); // Lấy message từ service
            System.err.println("Error deleting genre: " + e.getMessage());
        } catch (Exception e) { // Bắt lỗi không mong muốn khác
            redirectAttributes.addFlashAttribute("errorMessage", "Đã có lỗi xảy ra khi xóa thể loại.");
            System.err.println("Error deleting genre: " + e.getMessage());
        }
        return "redirect:/genres";
    }
}
