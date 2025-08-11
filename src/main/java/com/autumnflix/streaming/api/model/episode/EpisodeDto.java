package com.autumnflix.streaming.api.model.episode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EpisodeDto {

    private Long id;
    private Integer number;
    private String name;
    private String synopsis;
    private Integer duration;
}
