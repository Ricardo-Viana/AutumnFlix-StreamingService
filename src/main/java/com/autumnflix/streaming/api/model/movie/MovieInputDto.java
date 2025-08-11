package com.autumnflix.streaming.api.model.movie;

import com.autumnflix.streaming.api.model.entertainmentWork.EntertainmentWorkInputDto;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieInputDto extends EntertainmentWorkInputDto {

    @Positive
    private int duration;
}
