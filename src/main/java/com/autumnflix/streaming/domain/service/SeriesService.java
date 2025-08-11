package com.autumnflix.streaming.domain.service;

import com.autumnflix.streaming.domain.filter.EntertainmentWorkTypelessFilter;
import com.autumnflix.streaming.domain.model.EntertainmentWork;
import com.autumnflix.streaming.domain.model.Series;

import java.util.List;

public interface SeriesService {

    List<Series> getAll(EntertainmentWorkTypelessFilter filter);
    Series getSeries(Long seriesId);
    Series insert(Series series);
    void delete(Long seriesId);
    void associateGenre(Long seriesId, Long genreId);
    void disassociateGenre(Long seriesId, Long genreId);
}
