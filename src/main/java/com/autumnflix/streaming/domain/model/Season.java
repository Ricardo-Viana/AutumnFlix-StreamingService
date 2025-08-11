package com.autumnflix.streaming.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "season"
)
@Data
@EqualsAndHashCode(
        onlyExplicitlyIncluded = true
)
public class Season {

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
            name = "synopsis"
    )
    private String synopsis;

    @ManyToOne
    @JoinColumn(
            name = "series",
            foreignKey = @ForeignKey(
                    name = "fk_season_series_series_id"
            )
    )
    private Series series;

    @OneToMany(
            mappedBy = "season"
    )
    private List<Episode> episodes = new ArrayList<>();
}
