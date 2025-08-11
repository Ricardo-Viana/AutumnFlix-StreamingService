package com.autumnflix.streaming.api.model.entertainmentWork;

import com.autumnflix.streaming.domain.model.EntertainmentWorkType;
import lombok.Getter;
import lombok.Setter;

import java.time.Year;

@Getter
@Setter
public class EntertainmentWorkResumeDto extends EntertainmentWorkTypelessResumeDto{

    private EntertainmentWorkType type;
}
