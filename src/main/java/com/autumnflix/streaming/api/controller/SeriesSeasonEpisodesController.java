package com.autumnflix.streaming.api.controller;

import com.autumnflix.streaming.api.assembler.episode.EpisodeDtoAssembler;
import com.autumnflix.streaming.api.assembler.episode.EpisodeDtoDisassembler;
import com.autumnflix.streaming.api.model.episode.EpisodeDto;
import com.autumnflix.streaming.api.model.episode.EpisodeInputDto;
import com.autumnflix.streaming.domain.model.Episode;
import com.autumnflix.streaming.domain.model.Season;
import com.autumnflix.streaming.domain.service.EpisodeService;
import com.autumnflix.streaming.domain.service.SeasonService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/series/{seriesId}/seasons/{seasonNumber}/episodes")
public class SeriesSeasonEpisodesController {

    private SeasonService seasonService;
    private EpisodeService episodeService;
    private EpisodeDtoAssembler episodeDtoAssembler;
    private EpisodeDtoDisassembler episodeDtoDisassembler;


    public SeriesSeasonEpisodesController(SeasonService seasonService, EpisodeDtoAssembler episodeDtoAssembler,
                                          EpisodeDtoDisassembler episodeDtoDisassembler, EpisodeService episodeService) {
        this.seasonService = seasonService;
        this.episodeDtoAssembler = episodeDtoAssembler;
        this.episodeDtoDisassembler = episodeDtoDisassembler;
        this.episodeService = episodeService;
    }

    @GetMapping
    public List<EpisodeDto> getBySeriesIdAndSeasonNumber(@PathVariable("seriesId") Long seriesId,
                                                         @PathVariable("seasonNumber") Integer seasonNumber) {
        Season season = seasonService.getBySeriesIdAndSeasonNumber(seriesId, seasonNumber);

        return episodeDtoAssembler.toCollectionDto(season.getEpisodes());
    }

    @GetMapping("/{episodeNumber}")
    public EpisodeDto getBySeriesIdSeasonNumberAndEpisodeNumber(@PathVariable("seriesId") Long seriesId,
                                                                @PathVariable("seasonNumber") Integer seasonNumber,
                                                                @PathVariable("episodeNumber") Integer episodeNumber) {

        Season season = seasonService.getBySeriesIdAndSeasonNumber(seriesId, seasonNumber);

        return episodeDtoAssembler.toDto(episodeService.getBySeasonAndEpisodeNumber(season, episodeNumber));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EpisodeDto add(@PathVariable("seriesId") Long seriesId,
                          @PathVariable("seasonNumber") Integer seasonNumber,
                          @RequestBody @Valid EpisodeInputDto episodeInputDto) {

        Season season = seasonService.getBySeriesIdAndSeasonNumber(seriesId, seasonNumber);
        Episode episode = episodeDtoDisassembler.toEntityObject(episodeInputDto);

        episode.setSeason(season);

        return episodeDtoAssembler.toDto(episodeService.insert(episode));
    }

    @PutMapping("/{episodeNumber}")
    public EpisodeDto update(@PathVariable("seriesId") Long seriesId,
                             @PathVariable("seasonNumber") Integer seasonNumber,
                             @PathVariable("episodeNumber") Integer episodeNumber,
                             @RequestBody @Valid EpisodeInputDto episodeInputDto) {

        Season season = seasonService.getBySeriesIdAndSeasonNumber(seriesId, seasonNumber);
        Episode existingEpisode = episodeService.getBySeasonAndEpisodeNumber(season, episodeNumber);

        episodeDtoDisassembler.copyToEntityObject(episodeInputDto, existingEpisode);

        return episodeDtoAssembler.toDto(episodeService.insert(existingEpisode));
    }

    @DeleteMapping("/{episodeNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("seriesId") Long seriesId,
                       @PathVariable("seasonNumber") Integer seasonNumber,
                       @PathVariable("episodeNumber") Integer episodeNumber) {

        Season season = seasonService.getBySeriesIdAndSeasonNumber(seriesId, seasonNumber);

        episodeService.delete(season, episodeNumber);
    }
}
