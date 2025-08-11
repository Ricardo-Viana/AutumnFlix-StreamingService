package com.autumnflix.streaming.domain.service;

import com.autumnflix.streaming.domain.model.Episode;
import com.autumnflix.streaming.domain.model.Season;

public interface EpisodeService {

    Episode getBySeasonAndEpisodeNumber(Season season, Integer episodeNumber);

    Episode insert(Episode episode);

    void delete(Season season, Integer episodeNumber);
}
