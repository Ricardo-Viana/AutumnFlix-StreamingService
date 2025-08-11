package com.autumnflix.streaming.api.model.entertainmentWork;

import com.autumnflix.streaming.api.model.genre.GenreDto;
import lombok.Getter;
import lombok.Setter;

import java.time.Year;
import java.util.List;

@Getter
@Setter
public abstract class EntertainmentWorkTypelessDto {

    private Long id;

    private String name;

    private String synopsis;

    private Integer relevance;

    private Year releaseYear;

    private String parentalRating;

    private List<GenreDto> genres;
}
