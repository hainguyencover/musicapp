package com.example.musicapp.service.impl;

import com.example.musicapp.entity.Artist;
import com.example.musicapp.entity.Genre;
import com.example.musicapp.exception.DeletionBlockedException;
import com.example.musicapp.exception.DuplicateNameException;
import com.example.musicapp.repository.ArtistRepository;
import com.example.musicapp.repository.SongRepository;
import com.example.musicapp.service.IArtistService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ArtistServiceImpl implements IArtistService {
    private final ArtistRepository artistRepository;
    private final SongRepository songRepository; // Inject SongRepository

    // Constructor Injection: Tiêm ArtistRepository vào Service
    public ArtistServiceImpl(ArtistRepository artistRepository, SongRepository songRepository) {
        this.artistRepository = artistRepository;
        this.songRepository = songRepository;
    }

    @Override
    public List<Artist> findAll() {
        return artistRepository.findAll();
    }

    @Override
    public Page<Artist> findAll(Pageable pageable) { // Implement phương thức mới
        return artistRepository.findAll(pageable);
    }

    @Override
    public Optional<Artist> findById(Long id) {
        return artistRepository.findById(id);
    }

    @Override
    @Transactional // Đảm bảo các thao tác DB trong phương thức này là một transaction
    public Artist save(Artist artist) {
        // Có thể thêm logic kiểm tra nghiệp vụ ở đây trước khi lưu
        // Ví dụ: kiểm tra tên nghệ sĩ có hợp lệ không, có trùng không,...
        boolean nameExists;
        if (artist.getId() == null) {
            // Trường hợp TẠO MỚI
            nameExists = artistRepository.existsByNameIgnoreCase(artist.getName());
        } else {
            // Trường hợp CẬP NHẬT
            // Kiểm tra xem có nghệ sĩ KHÁC có tên trùng không
            nameExists = artistRepository.existsByNameIgnoreCaseAndIdNot(artist.getName(), artist.getId());
        }

        if (nameExists) {
            throw new DuplicateNameException("Tên nghệ sĩ '" + artist.getName() + "' đã tồn tại.");
        }
        return artistRepository.save(artist);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        // Kiểm tra xem có bài hát nào liên kết không
        if (songRepository.existsByArtistId(id)) {
            throw new DeletionBlockedException("Không thể xóa nghệ sĩ này vì có bài hát đang liên kết.");
        }
        // Chỉ xóa nếu không có bài hát nào liên kết
        artistRepository.deleteById(id);
    }
}
