package com.autumnflix.streaming.api.model.series;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeriesIdDto {

    @NotNull
    private Long id;
}
