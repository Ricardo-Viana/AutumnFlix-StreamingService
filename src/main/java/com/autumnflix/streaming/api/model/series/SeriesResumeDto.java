package com.autumnflix.streaming.api.model.series;

import com.autumnflix.streaming.api.model.entertainmentWork.EntertainmentWorkResumeDto;
import com.autumnflix.streaming.api.model.entertainmentWork.EntertainmentWorkTypelessResumeDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeriesResumeDto extends EntertainmentWorkTypelessResumeDto {

    private Integer numOfSeasons;
}
