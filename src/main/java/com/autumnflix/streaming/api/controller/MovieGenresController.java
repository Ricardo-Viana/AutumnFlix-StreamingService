package com.autumnflix.streaming.api.controller;

import com.autumnflix.streaming.api.assembler.genre.GenreDtoAssembler;
import com.autumnflix.streaming.api.model.genre.GenreDto;
import com.autumnflix.streaming.domain.model.Movie;
import com.autumnflix.streaming.domain.service.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies/{movieId}/genres")
public class MovieGenresController {

    private MovieService movieService;
    private GenreDtoAssembler genreDTOAssembler;

    public MovieGenresController(GenreDtoAssembler genreDTOAssembler, MovieService movieService) {
        this.genreDTOAssembler = genreDTOAssembler;
        this.movieService = movieService;
    }

    @GetMapping
    public List<GenreDto> getAllByGenreId(@PathVariable("movieId") Long movieId) {
        Movie movie = movieService.getMovie(movieId);

        return genreDTOAssembler.toCollectionDto(movie.getGenres());
    }

    @PutMapping("/{genreId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void associate(@PathVariable("movieId") Long movieId, @PathVariable("genreId") Long genreId) {
        movieService.associateGenre(movieId, genreId);
    }

    @DeleteMapping("/{genreId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disassociate(@PathVariable("movieId") Long movieId, @PathVariable("genreId") Long genreId) {
        movieService.disassociateGenre(movieId, genreId);
    }
}
