package com.example.musicapp.controller.web;

import com.example.musicapp.dto.ArtistDTO;
import com.example.musicapp.dto.GenreDTO;
import com.example.musicapp.entity.Artist;
import com.example.musicapp.entity.Genre;
import com.example.musicapp.entity.Song;
import com.example.musicapp.exception.ResourceNotFoundException;
import com.example.musicapp.service.IArtistService;
import com.example.musicapp.service.IGenreService;
import com.example.musicapp.service.ISongService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/explore") // URL dành riêng cho User xem
public class ExploreController {

    private final ISongService songService;
    private final IArtistService artistService;
    private final IGenreService genreService;

    public ExploreController(ISongService songService, IArtistService artistService, IGenreService genreService) {
        this.songService = songService;
        this.artistService = artistService;
        this.genreService = genreService;
    }

    /**
     * Hiển thị danh sách BÀI HÁT cho USER (có tìm kiếm, phân trang)
     */
    @GetMapping("/songs")
    public String showUserSongList(Model model,
                                   @PageableDefault(size = 10, sort = "name") Pageable pageable,
                                   @RequestParam(required = false) String keyword) {
        // Tận dụng lại logic service đã viết
        Page<Song> songPage = songService.findAll(keyword, pageable);
        model.addAttribute("songPage", songPage);
        model.addAttribute("keyword", keyword);
        // Trả về view của USER
        return "user/list_songs"; // -> templates/user/list_songs.html
    }

    /**
     * Hiển thị danh sách NGHỆ SĨ cho USER (có tìm kiếm, phân trang)
     */
    @GetMapping("/artists")
    public String showUserArtistList(Model model,
                                     @PageableDefault(size = 10, sort = "name") Pageable pageable,
                                     @RequestParam(required = false) String keyword) {

        Page<Artist> artistPageEntity = artistService.findAll(keyword, pageable);

        // (Tương tự logic trong ArtistController cũ, map DTO để gửi đi)
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

        // Trả về view của USER
        return "user/list_artists"; // -> templates/user/list_artists.html
    }

    @GetMapping("/genres")
    public String showUserGenreList(Model model,
                                    @PageableDefault(size = 10, sort = "name") Pageable pageable,
                                    @RequestParam(required = false) String keyword) {

        Page<Genre> genrePageEntity = genreService.findAll(keyword, pageable);

        // (Tương tự logic trong ArtistController cũ, map DTO để gửi đi)
        List<GenreDTO> dtoList = genrePageEntity.getContent().stream()
                .map(genre -> {
                    GenreDTO dto = new GenreDTO();
                    dto.setId(genre.getId());
                    dto.setName(genre.getName());
                    return dto;
                })
                .collect(Collectors.toList());
        Page<GenreDTO> genrePageDTO = new PageImpl<>(dtoList, pageable, genrePageEntity.getTotalElements());

        model.addAttribute("genrePage", genrePageDTO);
        model.addAttribute("keyword", keyword);

        // Trả về view của USER
        return "user/list_genres"; // -> templates/user/list_genres.html
    }

    /**
     * MỚI: Hiển thị trang chi tiết một NGHỆ SĨ
     * (Bao gồm thông tin nghệ sĩ và danh sách bài hát của họ)
     */
    @GetMapping("/artists/{id}")
    public String showArtistDetail(@PathVariable("id") Long id,
                                   @PageableDefault(size = 10, sort = "name") Pageable pageable,
                                   Model model) {

        // 1. Lấy thông tin nghệ sĩ
        Artist artist = artistService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid artist Id:" + id));

        // 2. Lấy danh sách bài hát của nghệ sĩ đó (có phân trang)
        Page<Song> songPage = songService.findByArtistId(id, pageable);

        // 3. (Nâng cao) Lấy các thể loại liên quan
        List<Genre> relatedGenres = songService.findDistinctGenresByArtistId(id);

        model.addAttribute("artist", artist);
        model.addAttribute("songPage", songPage);
        model.addAttribute("relatedGenres", relatedGenres);

        return "user/artist_detail"; // -> Cần tạo file templates/user/artist_detail.html
    }

    @GetMapping("/genres/{id}")
    public String showGenreDetail(
            @PathVariable Long id,
            Model model,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {

        // 1. Lấy thông tin thể loại
        Genre genre = genreService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genre", "id", id));

        // 2. Lấy danh sách bài hát thuộc thể loại đó
        Page<Song> songPage = songService.findByGenreId(id, pageable);

        List<Artist> relatedArtists = songService.findDistinctArtistsByGenreId(id);
        // 3. Gửi dữ liệu ra view
        model.addAttribute("genre", genre); // Gửi thông tin thể loại (Tên)
        model.addAttribute("songPage", songPage); // Gửi danh sách bài hát
        model.addAttribute("relatedArtists", relatedArtists);
        return "user/genre_detail"; // -> templates/user/genre_detail.html
    }


    @GetMapping("/songs/{id}")
    public String showSongDetail(@PathVariable("id") Long id, Model model) {

        // 1. Lấy thông tin bài hát
        Song song = songService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid song Id:" + id));

        model.addAttribute("song", song);
        model.addAttribute("artist", song.getArtist());
        model.addAttribute("genre", song.getGenre());

        return "user/song_detail"; // -> Cần tạo file templates/user/song_detail.html
    }
}
