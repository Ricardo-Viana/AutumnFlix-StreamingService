package com.autumnflix.streaming.api.model.season;

import com.autumnflix.streaming.api.model.series.SeriesIdDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeasonInputDto {

    @NotNull
    @Positive
    private Integer number;

    @NotBlank
    private String synopsis;
}
