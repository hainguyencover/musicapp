package com.example.musicapp.controller.web;

import com.example.musicapp.service.IArtistService;
import com.example.musicapp.service.IGenreService;
import com.example.musicapp.service.ISongService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final ISongService songService;
    private final IArtistService artistService;
    private final IGenreService genreService;

    public DashboardController(ISongService songService, IArtistService artistService, IGenreService genreService) {
        this.songService = songService;
        this.artistService = artistService;
        this.genreService = genreService;
    }

    /**
     * Hiển thị trang dashboard chính cho người dùng đã đăng nhập.
     */
    @GetMapping("/dashboard")
    public String userDashboard(Model model) {
        // Lấy dữ liệu (sử dụng các phương thức findAll() trả về List)
        model.addAttribute("songs", songService.findAll());
        model.addAttribute("artists", artistService.findAll());
        model.addAttribute("genres", genreService.findAll());

        return "user/dashboard"; // Trả về templates/dashboard.html
    }
}
