package com.autumnflix.streaming.api.model.movie;

import com.autumnflix.streaming.api.model.entertainmentWork.EntertainmentWorkTypelessDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieDto extends EntertainmentWorkTypelessDto {

    private int duration;
}
