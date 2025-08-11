package com.autumnflix.streaming.api.controller;

import com.autumnflix.streaming.api.assembler.movie.MovieDtoAssembler;
import com.autumnflix.streaming.api.assembler.movie.MovieDtoDisassembler;
import com.autumnflix.streaming.api.model.movie.MovieDto;
import com.autumnflix.streaming.api.model.movie.MovieInputDto;
import com.autumnflix.streaming.api.assembler.movie.MovieResumeDtoAssembler;
import com.autumnflix.streaming.api.model.movie.MovieResumeDto;
import com.autumnflix.streaming.domain.exception.BusinessException;
import com.autumnflix.streaming.domain.exception.GenreNotFoundException;
import com.autumnflix.streaming.domain.filter.EntertainmentWorkFilter;
import com.autumnflix.streaming.domain.filter.EntertainmentWorkTypelessFilter;
import com.autumnflix.streaming.domain.model.Movie;
import com.autumnflix.streaming.domain.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private MovieDtoAssembler movieDTOAssembler;
    private MovieResumeDtoAssembler movieResumeDtoAssembler;
    private MovieDtoDisassembler movieDTODisassembler;
    private MovieService movieService;

    public MovieController(MovieDtoAssembler movieDTOAssembler, MovieDtoDisassembler movieDTODisassembler,
                           MovieService movieService, MovieResumeDtoAssembler movieResumeDtoAssembler) {
        this.movieDTOAssembler = movieDTOAssembler;
        this.movieDTODisassembler = movieDTODisassembler;
        this.movieService = movieService;
        this.movieResumeDtoAssembler = movieResumeDtoAssembler;
    }

    @GetMapping
    private List<MovieResumeDto> getAll(EntertainmentWorkTypelessFilter filter) {
        return movieResumeDtoAssembler.toCollectionDTO(movieService.getAll(filter));
    }

    @GetMapping("/{movieId}")
    private MovieDto getById(@PathVariable("movieId") Long movieId) {
        return movieDTOAssembler.toDto(movieService.getMovie(movieId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    private MovieDto add(@RequestBody @Valid MovieInputDto movieInputDto) {
        Movie movie = movieDTODisassembler.toEntityObject(movieInputDto);

        return movieDTOAssembler.toDto(movieService.insert(movie));
    }

    @PutMapping("/{movieId}")
    private MovieDto update(@PathVariable("movieId") Long movieId, @RequestBody @Valid MovieInputDto movieInputDTO) {
        try {
            Movie existingMovie = movieService.getMovie(movieId);

            movieDTODisassembler.copyToEntityObject(movieInputDTO, existingMovie);

            return movieDTOAssembler.toDto(movieService.insert(existingMovie));
        }
        catch (GenreNotFoundException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @DeleteMapping("/{movieId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    private void delete(@PathVariable("movieId") Long movieId) {
        movieService.delete(movieId);
    }
}
