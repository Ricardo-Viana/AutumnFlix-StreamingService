package com.autumnflix.streaming.api.controller;

import com.autumnflix.streaming.api.assembler.genre.GenreDtoAssembler;
import com.autumnflix.streaming.api.model.genre.GenreDto;
import com.autumnflix.streaming.domain.model.Series;
import com.autumnflix.streaming.domain.service.SeriesService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/series/{seriesId}/genres")
public class SeriesGenresController {

    private SeriesService seriesService;
    private GenreDtoAssembler genreDTOAssembler;

    public SeriesGenresController(GenreDtoAssembler genreDTOAssembler, SeriesService seriesService) {
        this.genreDTOAssembler = genreDTOAssembler;
        this.seriesService = seriesService;
    }

    @GetMapping
    public List<GenreDto> getAllByGenreId(@PathVariable("seriesId") Long seriesId) {
        Series series = seriesService.getSeries(seriesId);

        return genreDTOAssembler.toCollectionDto(series.getGenres());
    }

    @PutMapping("/{genreId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void associate(@PathVariable("seriesId") Long seriesId, @PathVariable("genreId") Long genreId) {
        seriesService.associateGenre(seriesId, genreId);
    }

    @DeleteMapping("/{genreId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disassociate(@PathVariable("seriesId") Long seriesId, @PathVariable("genreId") Long genreId) {
        seriesService.disassociateGenre(seriesId, genreId);
    }
}
