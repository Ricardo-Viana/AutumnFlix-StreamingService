package com.autumnflix.streaming.api.model.series;

import com.autumnflix.streaming.api.model.entertainmentWork.EntertainmentWorkTypelessDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SeriesDto extends EntertainmentWorkTypelessDto {

    private Integer numOfSeasons;
}
