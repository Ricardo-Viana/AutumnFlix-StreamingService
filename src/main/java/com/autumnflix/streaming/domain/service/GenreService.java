package com.autumnflix.streaming.domain.service;

import com.autumnflix.streaming.domain.exception.GenreNotFoundException;
import com.autumnflix.streaming.domain.model.Genre;

import java.util.List;

public  interface GenreService {

    List<Genre> getAll();

    Genre getGenre(Long genreId);

    Genre insert(Genre genre);

    void delete(Long genreId);
}
