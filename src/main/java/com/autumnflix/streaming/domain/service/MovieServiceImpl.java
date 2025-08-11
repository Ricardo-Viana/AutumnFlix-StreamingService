package com.autumnflix.streaming.domain.service;

import com.autumnflix.streaming.domain.exception.MovieNotFoundException;
import com.autumnflix.streaming.domain.filter.EntertainmentWorkTypelessFilter;
import com.autumnflix.streaming.domain.model.Genre;
import com.autumnflix.streaming.domain.model.Movie;
import com.autumnflix.streaming.domain.repository.MovieRepository;
import com.autumnflix.streaming.infraestructure.repository.specs.EntertainmentWorkSpecs;
import com.autumnflix.streaming.infraestructure.repository.specs.MovieSpecs;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.autumnflix.streaming.domain.model.EntertainmentWorkType.MOVIE;

@Service
public class MovieServiceImpl implements MovieService {

    private MovieRepository movieRepository;
    private EntertainmentWorkService entertainmentWorkService;
    private GenreService genreService;

    public MovieServiceImpl(MovieRepository movieRepository, EntertainmentWorkService entertainmentWorkService,
                            GenreService genreService) {
        this.movieRepository = movieRepository;
        this.entertainmentWorkService = entertainmentWorkService;
        this.genreService = genreService;
    }

    @Override
    public List<Movie> getAll(EntertainmentWorkTypelessFilter filter) {
        return movieRepository.findAll(MovieSpecs.usingFilter(filter));
    }

    @Override
    public Movie getMovie(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(movieId));
    }

    @Override
    @Transactional
    public Movie insert(Movie movie) {
        movie.setType(MOVIE);

        return movieRepository.save(movie);
    }

    @Override
    @Transactional
    public void delete(Long movieId) {
        Movie movie = getMovie(movieId);

        entertainmentWorkService.delete(movie.getId());
    }

    @Transactional
    public void associateGenre(Long movieId, Long genreId) {
        Movie movie = getMovie(movieId);
        Genre genre = genreService.getGenre(genreId);

        movie.addGenre(genre);
    }

    @Transactional
    public void disassociateGenre(Long movieId, Long genreId) {
        Movie movie = getMovie(movieId);
        Genre genre = genreService.getGenre(genreId);

        movie.removeGenre(genre);
    }
}
