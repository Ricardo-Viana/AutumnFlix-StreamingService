package com.autumnflix.streaming.domain.service;

import com.autumnflix.streaming.domain.model.Season;

public interface SeasonService {

    Season getBySeriesIdAndSeasonNumber(Long seriesId, Integer seasonNumber);
    Season insert(Season season);
    void delete(Long seriesId, Integer seasonNumber);
}
