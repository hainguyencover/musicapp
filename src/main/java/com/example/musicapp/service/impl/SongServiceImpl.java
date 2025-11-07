package com.example.musicapp.service.impl;

import com.example.musicapp.dto.SongDTO;
import com.example.musicapp.entity.Artist;
import com.example.musicapp.entity.Genre;
import com.example.musicapp.entity.Song;
import com.example.musicapp.exception.DuplicateNameException;
import com.example.musicapp.exception.ResourceNotFoundException;
import com.example.musicapp.repository.SongRepository;
import com.example.musicapp.service.IArtistService;
import com.example.musicapp.service.IGenreService;
import com.example.musicapp.service.ISongService;
import com.example.musicapp.service.storage.IFileStorageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class SongServiceImpl implements ISongService {
    private final SongRepository songRepository;
    private final IArtistService artistService; // Thêm dependency
    private final IGenreService genreService;   // Thêm dependency
    private final IFileStorageService fileStorageService; // Thêm dependency

    public SongServiceImpl(SongRepository songRepository,
                           IArtistService artistService,
                           IGenreService genreService,
                           IFileStorageService fileStorageService) {
        this.songRepository = songRepository;
        this.artistService = artistService;
        this.genreService = genreService;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public List<Song> findAll() {
        return songRepository.findAll();
    }

    @Override
    public Page<Song> findAll(String keyword, Pageable pageable) { // Implement phương thức mới
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Nếu có keyword, gọi phương thức tìm kiếm
            return songRepository.findByNameContainingIgnoreCase(keyword, pageable);
        } else {
            // Nếu không có keyword, gọi findAll mặc định
            return songRepository.findAll(pageable);
        }
    }

    @Override
    public Optional<Song> findById(Long id) {
        return songRepository.findById(id);
    }

    @Override
    @Transactional
    public Song save(SongDTO songDTO, MultipartFile songFile) {
        // 1. Validate file (có thể thực hiện ở Controller hoặc Service)
        if (songFile == null || songFile.isEmpty()) {
            throw new IllegalArgumentException("File nhạc không được để trống khi tạo mới.");
        }

        // KIỂM TRA TRÙNG TÊN BÀI HÁT (Tên + Nghệ sĩ)
        if (songRepository.existsByNameIgnoreCaseAndArtistId(songDTO.getName(), songDTO.getArtistId())) {
            throw new DuplicateNameException("Bài hát '" + songDTO.getName() + "' của nghệ sĩ này đã tồn tại.");
        }

        // 2. Lưu file
        String storedFilename = fileStorageService.store(songFile);

        // 3. Tìm Artist và Genre từ ID trong DTO
        Artist artist = artistService.findById(songDTO.getArtistId())
                .orElseThrow(() -> new ResourceNotFoundException("Artist", "id", songDTO.getArtistId()));
        Genre genre = genreService.findById(songDTO.getGenreId())
                .orElseThrow(() -> new ResourceNotFoundException("Genre", "id", songDTO.getGenreId()));

        // 4. Map DTO sang Entity
        Song song = new Song();
        song.setName(songDTO.getName());
        song.setArtist(artist);
        song.setGenre(genre);
        song.setFilePath(storedFilename);

        // 5. Lưu vào DB
        return songRepository.save(song);
    }

    @Override
    @Transactional
    public Song update(SongDTO songDTO, MultipartFile songFile) {
        // 1. Lấy Song hiện tại từ DB
        Song existingSong = songRepository.findById(songDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Song", "id", songDTO.getId()));

        // KIỂM TRA TRÙNG TÊN BÀI HÁT KHI UPDATE
        if (songRepository.existsByNameIgnoreCaseAndArtistIdAndIdNot(songDTO.getName(), songDTO.getArtistId(), songDTO.getId())) {
            throw new DuplicateNameException("Bài hát '" + songDTO.getName() + "' của nghệ sĩ này đã tồn tại.");
        }

        // 2. Xử lý file nếu có file mới được upload
        if (songFile != null && !songFile.isEmpty()) {
            // Xóa file cũ nếu có
            if (existingSong.getFilePath() != null && !existingSong.getFilePath().isEmpty()) {
                fileStorageService.delete(existingSong.getFilePath());
            }
            // Lưu file mới và cập nhật path
            String newFilename = fileStorageService.store(songFile);
            existingSong.setFilePath(newFilename);
        } // Nếu không upload file mới thì giữ nguyên filePath cũ

        // 3. Tìm Artist và Genre từ ID trong DTO
        Artist artist = artistService.findById(songDTO.getArtistId())
                .orElseThrow(() -> new ResourceNotFoundException("Artist", "id", songDTO.getArtistId()));
        Genre genre = genreService.findById(songDTO.getGenreId())
                .orElseThrow(() -> new ResourceNotFoundException("Genre", "id", songDTO.getGenreId()));

        // 4. Cập nhật thông tin từ DTO vào Entity
        existingSong.setName(songDTO.getName());
        existingSong.setArtist(artist);
        existingSong.setGenre(genre);
        // filePath đã được xử lý ở trên

        // 5. Lưu lại vào DB
        return songRepository.save(existingSong);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        // Lấy thông tin Song trước khi xóa để có filePath
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Song", "id", id));

        String filePath = song.getFilePath();

        // Xóa record trong DB trước
        songRepository.deleteById(id);

        // Sau đó xóa file vật lý (nếu có)
        if (filePath != null && !filePath.isEmpty()) {
            fileStorageService.delete(filePath);
        }
    }

    // Implement các phương thức tìm kiếm khác nếu có trong interface
}
