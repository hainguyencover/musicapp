package com.example.musicapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "accounts")
@Getter
@Setter
public class Account extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Enumerated(EnumType.STRING) // Lưu tên của Enum (USER, ADMIN)
    @Column(nullable = false)
    private Role role = Role.USER;

    // --- Relationships ---

    // Một Account có nhiều Playlist
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Playlist> playlists;

    // Một Account có nhiều bài hát yêu thích (UserFavorite)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserFavorite> userFavorites;

    // Một Account có nhiều lịch sử nghe
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ListeningHistory> listeningHistories;

    // Một Account có nhiều phản hồi
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AdminFeedback> feedbacks;

}
