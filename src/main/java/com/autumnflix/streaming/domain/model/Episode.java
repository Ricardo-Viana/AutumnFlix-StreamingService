package com.autumnflix.streaming.domain.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(
        name = "episode"
)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Episode {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @EqualsAndHashCode.Include
    private Long id;

    @Column(
            name = "number"
    )
    private Integer number;

    @Column(
            name = "name"
    )
    private String name;

    @Column(
            name = "synopsis"
    )
    private String synopsis;

    @Column(
            name = "duration"
    )
    private Integer duration;

    @ManyToOne
    @JoinColumn(
            name = "season",
            foreignKey = @ForeignKey(
                    name = "fk_episode_season_season_id"
            )
    )
    private Season season;
}
