package com.example.musicapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users") // Tên bảng trong DB
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 64) // Độ dài 64 cho BCrypt hash
    private String password;

    @Column(nullable = false)
    private boolean enabled = true; // Cờ để kích hoạt/vô hiệu hóa tài khoản

    /**
     * Quản lý danh sách vai trò của người dùng.
     * Sử dụng @ElementCollection để tạo bảng phụ (user_roles) tự động.
     * FetchType.EAGER là quan trọng ở đây, vì Spring Security cần tải vai trò
     * CÙNG LÚC với User khi xác thực.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING) // Lưu tên của Enum (ví dụ: "ROLE_ADMIN")
    @Column(name = "role", nullable = false)
    private Set<RoleName> roles = new HashSet<>();

    /**
     * Danh sách các bài hát User này đã yêu thích.
     * Mối quan hệ Nhiều-Nhiều (ManyToMany).
     * LAZY: Chỉ tải danh sách này khi gọi user.getFavoriteSongs()
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_favorite_songs", // Tên bảng liên kết (DB)
            joinColumns = @JoinColumn(name = "user_id"), // Cột trỏ về User
            inverseJoinColumns = @JoinColumn(name = "song_id") // Cột trỏ về Song
    )
    private Set<Song> favoriteSongs = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_favorite_artists", // Tên bảng liên kết (DB)
            joinColumns = @JoinColumn(name = "user_id"), // Cột trỏ về User
            inverseJoinColumns = @JoinColumn(name = "artist_id") // Cột trỏ về Artist
    )
    private Set<Artist> favoriteArtists = new HashSet<>();

    // Constructor (tùy chọn)
    public User(String username, String password, boolean enabled) {
        this.username = username;
        this.password = password;
        this.enabled = enabled;
    }
}
