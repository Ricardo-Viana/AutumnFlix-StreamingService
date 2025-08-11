package com.autumnflix.streaming.domain.exception;

public class SeriesNotFoundException extends EntityNotFoundException {

    public SeriesNotFoundException(String message) {
        super(message);
    }

    public SeriesNotFoundException(Long seriesId) {
        this("Series with id %d doesn't exist".formatted(seriesId));
    }
}
