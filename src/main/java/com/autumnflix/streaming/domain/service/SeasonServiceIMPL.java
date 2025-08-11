package com.autumnflix.streaming.domain.service;

import com.autumnflix.streaming.domain.exception.BusinessException;
import com.autumnflix.streaming.domain.exception.EntityBeingUsedException;
import com.autumnflix.streaming.domain.exception.SeasonNotFoundException;
import com.autumnflix.streaming.domain.model.Season;
import com.autumnflix.streaming.domain.model.Series;
import com.autumnflix.streaming.domain.repository.SeasonRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SeasonServiceIMPL implements SeasonService {

    private SeasonRepository seasonRepository;
    private SeriesService seriesService;

    public SeasonServiceIMPL(SeasonRepository seasonRepository, SeriesService seriesService) {
        this.seasonRepository = seasonRepository;
        this.seriesService = seriesService;
    }

    @Override
    public Season getBySeriesIdAndSeasonNumber(Long seriesId, Integer seasonNumber) {
        Series series = seriesService.getSeries(seriesId);

        return seasonRepository.findByNumberAndSeries(seasonNumber, series)
                .orElseThrow(() -> new SeasonNotFoundException(seasonNumber, seriesId));
    }

    @Override
    @Transactional
    public Season insert(Season season) {
        seasonRepository.detach(season);

        Optional<Season> existingSeason = seasonRepository
                .findByNumberAndSeries(season.getNumber(), season.getSeries());

        if (existingSeason.isPresent() && !existingSeason.get().equals(season)) {
            throw new BusinessException("Season %d already exists for the series with id %d"
                    .formatted(season.getNumber(), season.getSeries().getId()));
        }

        return seasonRepository.save(season);
    }

    @Override
    @Transactional
    public void delete(Long seriesId, Integer seasonNumber) {
        Season season = getBySeriesIdAndSeasonNumber(seriesId, seasonNumber);

        try {
            seasonRepository.delete(season);
            seasonRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new EntityBeingUsedException("Season", seasonNumber);
        }
    }
}
