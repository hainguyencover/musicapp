package com.example.musicapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "playlist_songs", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"playlist_id", "song_id"})
})
@Getter
@Setter
public class PlaylistSong extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;
}
