package com.autumnflix.streaming.domain.exception;

public class EpisodeNotFoundException extends  EntityNotFoundException {
    public EpisodeNotFoundException(String message) {
        super(message);
    }

    public EpisodeNotFoundException(Integer episodeNumber, Integer seasonNumber, Long seriesId) {
        this("The episode %d in season %d from the series of id %d doesnt exists"
                .formatted(episodeNumber, seasonNumber, seriesId));
    }
}
