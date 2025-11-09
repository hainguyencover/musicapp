package com.example.musicapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "songs")
@Getter
@Setter
public class Song extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int duration;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "image_url")
    private String imageUrl;

    // --- Relationships ---

    // Nhiều bài hát (Song) thuộc về một nghệ sĩ (Artist)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    // Nhiều bài hát (Song) thuộc về một thể loại (Genre)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;

    // Một bài hát (Song) có thể ở trong nhiều playlist (PlaylistSong)
    @OneToMany(mappedBy = "song", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PlaylistSong> playlistSongs;

    // Một bài hát (Song) có thể được nhiều người yêu thích (UserFavorite)
    @OneToMany(mappedBy = "song", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserFavorite> userFavorites;

    // Một bài hát (Song) có thể có nhiều lượt nghe
    @OneToMany(mappedBy = "song", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ListeningHistory> listeningHistories;
}
