package com.example.musicapp.service.impl;

import com.example.musicapp.dto.UserRegistrationDTO;
import com.example.musicapp.entity.Artist;
import com.example.musicapp.entity.RoleName;
import com.example.musicapp.entity.Song;
import com.example.musicapp.entity.User;
import com.example.musicapp.exception.DuplicateNameException;
import com.example.musicapp.exception.ResourceNotFoundException;
import com.example.musicapp.repository.ArtistRepository;
import com.example.musicapp.repository.UserRepository;
import com.example.musicapp.repository.SongRepository;
import com.example.musicapp.service.IUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           SongRepository songRepository,
                           ArtistRepository artistRepository) { // Thêm vào
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
    }

    @Override
    @Transactional
    public void registerNewUser(UserRegistrationDTO dto) {
        // 1. Kiểm tra mật khẩu khớp
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            // Ném lỗi (Controller sẽ bắt)
            throw new IllegalArgumentException("Mật khẩu không khớp!");
        }

        // 2. Kiểm tra tên đăng nhập tồn tại
        if (userRepository.existsByUsernameIgnoreCase(dto.getUsername())) {
            // Ném lỗi (Controller sẽ bắt)
            throw new DuplicateNameException("Tên đăng nhập '" + dto.getUsername() + "' đã tồn Tồn tại.");
        }

        // 3. Tạo User mới
        User newUser = new User();
        newUser.setUsername(dto.getUsername());
        // 4. Mã hóa mật khẩu
        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        newUser.setEnabled(true); // Kích hoạt tài khoản
        // 5. Gán vai trò USER
        newUser.setRoles(Set.of(RoleName.ROLE_USER));

        // 6. Lưu vào DB
        userRepository.save(newUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true) // Chỉ đọc
    public Set<Long> getFavoriteSongIds(String username) {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Lấy danh sách ID
        return user.getFavoriteSongs().stream()
                .map(Song::getId)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void addFavoriteSong(String username, Long songId) {
        // Tìm user và bài hát
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song", "id", songId));

        // Thêm bài hát vào Set (Set tự động xử lý trùng lặp)
        user.getFavoriteSongs().add(song);

        userRepository.save(user); // Lưu lại thay đổi
    }

    @Override
    @Transactional
    public void removeFavoriteSong(String username, Long songId) {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song", "id", songId));

        // Xóa bài hát khỏi Set
        user.getFavoriteSongs().remove(song);

        userRepository.save(user); // Lưu lại thay đổi
    }

    @Override
    @Transactional(readOnly = true) // Cần @Transactional để fetch Set<Song>
    public Set<Song> getFavoriteSongs(String username) {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Trả về Set<Song> đầy đủ
        // Nhờ @Transactional, Hibernate sẽ tự tải danh sách favoriteSongs khi ta gọi
        return user.getFavoriteSongs();
    }

    @Override
    @Transactional(readOnly = true) // Chỉ đọc
    public Set<Long> getFavoriteArtistIds(String username) {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Lấy danh sách ID
        return user.getFavoriteArtists().stream()
                .map(Artist::getId)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void addFavoriteArtist(String username, Long artistId) {
        // Tìm user và bài hát
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist", "id", artistId));

        // Thêm bài hát vào Set (Set tự động xử lý trùng lặp)
        user.getFavoriteArtists().add(artist);

        userRepository.save(user); // Lưu lại thay đổi
    }

    @Override
    @Transactional
    public void removeFavoriteArtist(String username, Long artistId) {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist", "id", artistId));

        // Xóa bài hát khỏi Set
        user.getFavoriteArtists().remove(artist);

        userRepository.save(user); // Lưu lại thay đổi
    }

    @Override
    @Transactional(readOnly = true) // Cần @Transactional để fetch Set<Song>
    public Set<Artist> getFavoriteArtists(String username) {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Trả về Set<Song> đầy đủ
        // Nhờ @Transactional, Hibernate sẽ tự tải danh sách favoriteSongs khi ta gọi
        return user.getFavoriteArtists();
    }

    @Override
    public long count() {
        // Chỉ cần gọi JpaRepository.count()
        return userRepository.count();
    }
}
