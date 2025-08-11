package com.autumnflix.streaming.api.model.entertainmentWork;

import com.autumnflix.streaming.core.validation.annotation.ReleaseYearConstraint;
import com.autumnflix.streaming.domain.model.Rating;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Year;

@Getter
@Setter
public class EntertainmentWorkInputDto {

    @NotBlank
    private String name;

    @NotBlank
    private String synopsis;

    @NotNull
    private Integer relevance;

    @ReleaseYearConstraint
    private Year releaseYear;

    @NotNull
    private Rating parentalRating;
}
