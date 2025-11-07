package com.example.musicapp.controller.web;

import com.example.musicapp.entity.Song;
import com.example.musicapp.exception.ResourceNotFoundException;
import com.example.musicapp.service.ISongService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PlayerController {
    private final ISongService songService;

    public PlayerController(ISongService songService) {
        this.songService = songService;
    }

    /**
     * Hiển thị trang phát nhạc cho một bài hát cụ thể.
     *
     * @param id    ID của bài hát
     * @param model Model
     * @return Tên view "play"
     */
    @GetMapping("/play/{id}")
    public String showPlayerPage(@PathVariable Long id, Model model) {
        // Tìm bài hát theo ID
        Song song = songService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Song", "id", id));

        // Gửi thông tin bài hát (tên, nghệ sĩ, đường dẫn file) ra view
        model.addAttribute("song", song);

        return "play"; // Trả về file templates/play.html
    }
}
