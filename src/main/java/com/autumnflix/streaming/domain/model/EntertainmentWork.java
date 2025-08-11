package com.autumnflix.streaming.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "entertain_work"
)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Inheritance(strategy = InheritanceType.JOINED)
public class EntertainmentWork {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @EqualsAndHashCode.Include
    private Long id;

    @Column(
            nullable = false
    )
    private String name;

    @Column(
            nullable = false
    )
    private String synopsis;

    @Column(
            nullable = false
    )
    private Integer relevance;

    @Column(
            nullable = false
    )
    private Year releaseYear;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false
    )
    private Rating parentalRating;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false
    )
    private EntertainmentWorkType type;

    @ManyToMany
    @JoinTable(
            name = "entertain_work_genres",
            joinColumns = @JoinColumn(
                    name = "entertain_work_id",
                    foreignKey = @ForeignKey(
                            name = "fk_entertain_work_genres_entertain_work_entertain_work_id"
                    )
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "genre_id",
                    foreignKey = @ForeignKey(
                            name = "fk_entertain_work_genres_genre_genre_id"
                    )
            )
    )
    private Set<Genre> genres = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_watched_entertain_work",
            joinColumns = @JoinColumn(
                    name = "entertainment_work_id",
                    foreignKey = @ForeignKey(
                            name = "fk_user_watched_entertain_work_entertain_work_entertain_work_id"
                    )
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "user_id"
            )
    )
    private List<User> viewers = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "user_favorites_entertain_work",
            joinColumns = @JoinColumn(
                    name = "entertainment_work_id",
                    foreignKey = @ForeignKey(
                            name = "fk_user_favorites_ew_entertain_work_entertain_work_id"
                    )
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "user_id"
            )
    )
    private Set<User> fans = new HashSet<>();

    public void addFan(User user){
        this.getFans().add(user);
    }

    public void removeFan(User user){
        this.getFans().remove(user);
    }

    public void addViewer(User user){
        this.getViewers().add(user);
    }

    public void removeViewer(User user) {
        this.getViewers().remove(user);
    }
      
    public void addGenre(Genre genre) {
        getGenres().add(genre);
    }

    public void removeGenre(Genre genre) {
        getGenres().remove(genre);
    }
}
