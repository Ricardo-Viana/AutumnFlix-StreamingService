package com.autumnflix.streaming.domain.filter;

import com.autumnflix.streaming.domain.model.EntertainmentWorkType;
import com.autumnflix.streaming.domain.model.Rating;
import lombok.Getter;
import lombok.Setter;

import java.time.Year;

@Getter
@Setter
public class EntertainmentWorkFilter extends EntertainmentWorkTypelessFilter{

    private EntertainmentWorkType type;

}
