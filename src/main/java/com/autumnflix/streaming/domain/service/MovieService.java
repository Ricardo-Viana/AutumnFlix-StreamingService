package com.autumnflix.streaming.domain.service;

import com.autumnflix.streaming.domain.filter.EntertainmentWorkFilter;
import com.autumnflix.streaming.domain.filter.EntertainmentWorkTypelessFilter;
import com.autumnflix.streaming.domain.model.Movie;

import java.util.List;

public interface MovieService {

    List<Movie> getAll(EntertainmentWorkTypelessFilter filter);
    Movie getMovie(Long entertainmentWorkId);
    Movie insert(Movie movie);
    void delete(Long entertainmentWorkId);
    void associateGenre(Long movieId, Long genreId);
    void disassociateGenre(Long movieId, Long genreId);
}
