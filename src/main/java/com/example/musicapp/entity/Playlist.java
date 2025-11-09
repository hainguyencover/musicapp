package com.example.musicapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "playlists")
@Getter
@Setter
public class Playlist extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String description;

    // --- Relationships ---

    // Nhiều Playlist thuộc về một Account
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    // Một Playlist có nhiều bài hát (PlaylistSong)
    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PlaylistSong> playlistSongs;
}
