package com.autumnflix.streaming.api.model.entertainmentWork;

import com.autumnflix.streaming.domain.model.EntertainmentWorkType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntertainmentWorkDto extends EntertainmentWorkTypelessDto {

    private EntertainmentWorkType type;

}
