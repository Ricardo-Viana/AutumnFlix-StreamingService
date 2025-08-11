package com.autumnflix.streaming.api.controller;

import com.autumnflix.streaming.api.assembler.season.SeasonDtoAssembler;
import com.autumnflix.streaming.api.assembler.season.SeasonDtoDisassembler;
import com.autumnflix.streaming.api.model.season.SeasonDto;
import com.autumnflix.streaming.api.model.season.SeasonInputDto;
import com.autumnflix.streaming.domain.model.Season;
import com.autumnflix.streaming.domain.model.Series;
import com.autumnflix.streaming.domain.service.SeasonService;
import com.autumnflix.streaming.domain.service.SeriesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/series/{seriesId}/seasons")
public class SeriesSeasonsController {

    private SeriesService seriesService;
    private SeasonService seasonService;
    private SeasonDtoAssembler seasonDtoAssembler;
    private SeasonDtoDisassembler seasonDtoDisassembler;

    public SeriesSeasonsController(SeriesService seriesService, SeasonDtoAssembler seasonDtoAssembler,
                                   SeasonDtoDisassembler seasonDtoDisassembler, SeasonService seasonService) {
        this.seriesService = seriesService;
        this.seasonDtoAssembler = seasonDtoAssembler;
        this.seasonDtoDisassembler = seasonDtoDisassembler;
        this.seasonService = seasonService;
    }

    @GetMapping
    public List<SeasonDto> getAllBySeriesId(@PathVariable("seriesId") Long seriesId) {

        Series series = seriesService.getSeries(seriesId);

        return seasonDtoAssembler.toCollectionDto(series.getSeasons());
    }

    @GetMapping("/{seasonNumber}")
    public SeasonDto getBySeriesIdAndSeasonNumber(@PathVariable("seriesId") Long seriesId,
                                                  @PathVariable("seasonNumber") Integer seasonNumber) {
        return seasonDtoAssembler.toDto(seasonService.getBySeriesIdAndSeasonNumber(seriesId, seasonNumber));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SeasonDto add(@PathVariable("seriesId") Long seriesId,
                               @RequestBody @Valid SeasonInputDto seasonInputDto) {

        Series series = seriesService.getSeries(seriesId);
        Season season = seasonDtoDisassembler.toEntityObject(seasonInputDto);

        season.setSeries(series);

        return seasonDtoAssembler.toDto(seasonService.insert(season));
    }

    @PutMapping("/{seasonNumber}")
    public SeasonDto update(@PathVariable("seriesId") Long seriesId, @PathVariable("seasonNumber") Integer seasonNumber,
                            @RequestBody @Valid SeasonInputDto seasonInputDto) {

        Season existingSeason = seasonService.getBySeriesIdAndSeasonNumber(seriesId, seasonNumber);

        seasonDtoDisassembler.copyToEntityObject(seasonInputDto, existingSeason);

        return seasonDtoAssembler.toDto(seasonService.insert(existingSeason));
    }

    @DeleteMapping("/{seasonNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("seriesId") Long seriesId,
                       @PathVariable("seasonNumber") Integer seasonNumber) {

        seasonService.delete(seriesId, seasonNumber);
    }
}
