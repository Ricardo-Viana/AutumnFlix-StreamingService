package com.autumnflix.streaming.api.model.episode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EpisodeInputDto {

    @NotBlank
    private String name;

    @NotNull
    @Positive
    private Integer number;

    @NotBlank
    private String synopsis;

    @NotNull
    @Positive
    private Integer duration;
}
