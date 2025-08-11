package com.autumnflix.streaming.domain.exception;

public class SeasonNotFoundException extends EntityNotFoundException {
    public SeasonNotFoundException(String message) {
        super(message);
    }

    public SeasonNotFoundException(Integer seasonNumber, Long seriesId) {
        this("The season %d from the series of id %d doesn't exists".formatted(seasonNumber, seriesId));
    }
}
