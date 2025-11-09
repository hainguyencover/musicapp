package com.example.musicapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_favorites", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"account_id", "song_id"})
})
@Getter
@Setter
public class UserFavorite extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;
}
