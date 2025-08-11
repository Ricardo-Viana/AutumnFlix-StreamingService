package com.autumnflix.streaming.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "series"
)
@Data
@PrimaryKeyJoinColumn(name = "id")
@EqualsAndHashCode(callSuper = true)
public class Series extends EntertainmentWork{
    @OneToMany(
            mappedBy = "series"
    )
    private Set<Season> seasons = new HashSet<>();

    public Integer numOfSeasons() {
        return seasons.size();
    }
}
