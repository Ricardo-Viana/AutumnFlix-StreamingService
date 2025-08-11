package com.autumnflix.streaming.api.model.season;

import com.autumnflix.streaming.api.model.series.SeriesDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeasonDto {

    private Long id;
    private Integer number;
    private String synopsis;
}
