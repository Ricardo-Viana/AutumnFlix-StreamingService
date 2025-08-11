package com.autumnflix.streaming.domain.filter;

import com.autumnflix.streaming.domain.model.Rating;
import lombok.Getter;
import lombok.Setter;

import java.time.Year;

@Getter
@Setter
public class EntertainmentWorkTypelessFilter {

    private String name;

    private Integer relevance;

    private Year initialReleaseYear;

    private Year finalReleaseYear;

    private Rating rating;

    private Long genreId;
}
