package com.autumnflix.streaming.api.model.entertainmentWork;

import lombok.Getter;
import lombok.Setter;

import java.time.Year;

@Getter
@Setter
public abstract class EntertainmentWorkTypelessResumeDto {

    private Long id;

    private String name;

    private String synopsis;

    private String parentalRating;

    private Year releaseYear;

    private Integer relevance;
}
