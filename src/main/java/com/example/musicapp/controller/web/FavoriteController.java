package com.example.musicapp.controller.web;

import com.example.musicapp.entity.Song;
import com.example.musicapp.service.IUserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Set;

@Controller
public class FavoriteController {

    private final IUserService userService;

    public FavoriteController(IUserService userService) {
        this.userService = userService;
    }

    /**
     * Hiển thị trang "Bài hát yêu thích" của người dùng.
     */
    @GetMapping("/favorites")
    public String showFavoritesPage(Model model, Principal principal) {
        // 1. Lấy username của người đang đăng nhập
        String username = principal.getName();

        // 2. Gọi service mới để lấy Set<Song>
        Set<Song> favoriteSongs = userService.getFavoriteSongs(username);

        // 3. Đưa danh sách ra view
        model.addAttribute("songs", favoriteSongs);

        // 4. Trả về file view mới
        return "favorites"; // -> templates/favorites.html
    }
}
