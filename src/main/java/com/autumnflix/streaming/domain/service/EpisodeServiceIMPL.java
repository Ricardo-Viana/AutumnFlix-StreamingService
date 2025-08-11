package com.autumnflix.streaming.domain.service;

import com.autumnflix.streaming.domain.exception.BusinessException;
import com.autumnflix.streaming.domain.exception.EntityBeingUsedException;
import com.autumnflix.streaming.domain.exception.EpisodeNotFoundException;
import com.autumnflix.streaming.domain.model.Episode;
import com.autumnflix.streaming.domain.model.Season;
import com.autumnflix.streaming.domain.repository.EpisodeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EpisodeServiceIMPL implements EpisodeService {

    private EpisodeRepository episodeRepository;

    public EpisodeServiceIMPL(EpisodeRepository episodeRepository) {
        this.episodeRepository = episodeRepository;
    }

    @Override
    public Episode getBySeasonAndEpisodeNumber(Season season, Integer episodeNumber) {
        return episodeRepository.getEpisodesBySeasonAndNumber(season, episodeNumber)
                .orElseThrow(() -> new EpisodeNotFoundException(episodeNumber, season.getNumber(), season.getSeries().getId()));
    }

    @Override
    public Episode insert(Episode episode) {
        episodeRepository.detach(episode);

        Optional<Episode> existingEpisode =
                episodeRepository.getEpisodesBySeasonAndNumber(episode.getSeason(), episode.getNumber());


        if (existingEpisode.isPresent() && !existingEpisode.get().equals(episode)) {
            throw new BusinessException("The episode %d in season %d from the series of id %d already exists".formatted(
                    episode.getNumber(), episode.getSeason().getNumber(), episode.getSeason().getSeries().getId())
            );
        }

        return episodeRepository.save(episode);
    }

    @Override
    public void delete(Season season, Integer episodeNumber) {
        Episode episode = getBySeasonAndEpisodeNumber(season, episodeNumber);

        try {
            episodeRepository.delete(episode);
            episodeRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new EntityBeingUsedException("Episode", episodeNumber);
        }
    }
}
