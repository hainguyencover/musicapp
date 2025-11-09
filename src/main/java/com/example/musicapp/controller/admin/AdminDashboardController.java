package com.example.musicapp.controller.admin;

import com.example.musicapp.service.IArtistService;
import com.example.musicapp.service.IGenreService;
import com.example.musicapp.service.ISongService;
import com.example.musicapp.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin") // Tiền tố chung cho Admin
public class AdminDashboardController {

    private static final Logger logger = LoggerFactory.getLogger(AdminDashboardController.class);

    // (Inject các service bạn cần để thống kê, ví dụ:
    private final IUserService userService;
    private final ISongService songService;
    private final IArtistService artistService;
    private final IGenreService genreService;

    public AdminDashboardController(IUserService userService, ISongService songService, IArtistService artistService, IGenreService genreService) {
        this.userService = userService;
        this.songService = songService;
        this.artistService = artistService;
        this.genreService = genreService;
    }

    @GetMapping("/dashboard")
    public String showAdminDashboard(Model model) {
        logger.info("Đang tải Admin Dashboard");

        // Lấy dữ liệu thống kê (ví dụ)
        // (Bạn cần thêm các phương thức .count() vào Repository/Service)
        model.addAttribute("userCount", userService.count());
        model.addAttribute("songCount", songService.count());
        model.addAttribute("artistCount", artistService.count());
        model.addAttribute("genreCount", genreService.count());

        // Trả về view admin
        return "admin/dashboard"; // -> templates/admin/dashboard.html
    }
}
