package com.example.musicapp.controller.web;

import com.example.musicapp.dto.GenreDTO;
import com.example.musicapp.entity.Genre;
import com.example.musicapp.exception.DeletionBlockedException;
import com.example.musicapp.exception.DuplicateNameException;
import com.example.musicapp.exception.ResourceNotFoundException;
import com.example.musicapp.service.IGenreService;
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
@RequestMapping("/genres")
public class GenreController {
    private final IGenreService genreService;

    // THÊM LOGGER
    private static final Logger logger = LoggerFactory.getLogger(GenreController.class);

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
        logger.debug("Fetching genre list page {}", pageable.getPageNumber());
        Page<Genre> genrePageEntity = genreService.findAll(pageable);

        List<GenreDTO> dtoList = genrePageEntity.getContent().stream()
                .map(genre -> new GenreDTO(genre.getId(), genre.getName()))
                .collect(Collectors.toList());
        Page<GenreDTO> genrePageDTO = new PageImpl<>(dtoList, pageable, genrePageEntity.getTotalElements());

        model.addAttribute("genrePage", genrePageDTO);
        return "genre/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        logger.info("Displaying create genre form");
        model.addAttribute("genreDTO", new GenreDTO());
        return "genre/create";
    }

    @PostMapping("/create")
    public String createGenre(@Valid @ModelAttribute("genreDTO") GenreDTO genreDTO,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            logger.warn("Validation errors in createGenre form.");
            return "genre/create";
        }
        try {
            Genre genre = new Genre();
            genre.setName(genreDTO.getName());
            genreService.save(genre);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm thể loại thành công!");
            logger.info("Genre created successfully: {}", genre.getName());
            return "redirect:/genres";
        } catch (DuplicateNameException e) { // SỬA: Bắt DuplicateNameException
            logger.warn("Failed to create genre due to duplicate name: {}", e.getMessage());
            bindingResult.rejectValue("name", "error.genre.name.duplicate", e.getMessage());
            return "genre/create";
        } catch (Exception e) {
            logger.error("Error saving genre: {}", e.getMessage(), e);
            bindingResult.reject("error.generic", "Đã có lỗi xảy ra khi lưu thể loại.");
            return "genre/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        logger.info("Displaying edit form for genre ID {}", id);
        Genre genre = genreService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genre", "id", id));
        GenreDTO genreDTO = new GenreDTO(genre.getId(), genre.getName());
        model.addAttribute("genreDTO", genreDTO);
        return "genre/edit";
    }

    @PostMapping("/update")
    public String updateGenre(@Valid @ModelAttribute("genreDTO") GenreDTO genreDTO,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            logger.warn("Validation errors in updateGenre form for ID {}.", genreDTO.getId());
            return "genre/edit";
        }
        try {
            Genre existingGenre = genreService.findById(genreDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Genre", "id", genreDTO.getId()));
            existingGenre.setName(genreDTO.getName());
            genreService.save(existingGenre);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thể loại thành công!");
            logger.info("Genre updated successfully: {}", existingGenre.getName());
            return "redirect:/genres";
        } catch (DuplicateNameException e) { // SỬA: Bắt DuplicateNameException
            logger.warn("Failed to update genre ID {} due to duplicate name: {}", genreDTO.getId(), e.getMessage());
            bindingResult.rejectValue("name", "error.genre.name.duplicate", e.getMessage());
            return "genre/edit";
        } catch (Exception e) {
            logger.error("Error updating genre ID {}: {}", genreDTO.getId(), e.getMessage(), e);
            bindingResult.reject("error.generic", "Đã có lỗi xảy ra khi cập nhật thể loại.");
            return "genre/edit";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteGenre(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Attempting to delete genre ID {}", id);
        try {
            genreService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa thể loại thành công!");
            logger.info("Genre deleted successfully, ID {}", id);
        } catch (ResourceNotFoundException e) {
            logger.warn("Failed to delete genre: ID {} not found.", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thể loại để xóa!");
        } catch (DeletionBlockedException | DataIntegrityViolationException e) {
            logger.warn("Failed to delete genre ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            logger.error("Error deleting genre ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Đã có lỗi xảy ra khi xóa thể loại.");
        }
        return "redirect:/genres";
    }
}
