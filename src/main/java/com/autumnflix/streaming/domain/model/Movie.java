package com.autumnflix.streaming.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(
        name = "movie"
)
@Data
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "id")
public class Movie extends EntertainmentWork{
    @Column(
            name = "duration",
            nullable = false
    )
    private Integer duration;
}
