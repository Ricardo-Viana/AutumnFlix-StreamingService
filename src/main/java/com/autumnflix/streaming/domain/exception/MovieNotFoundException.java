package com.autumnflix.streaming.domain.exception;

public class MovieNotFoundException extends EntertainmentWorkNotFoundException {
    public MovieNotFoundException(String message) {
        super(message);
    }

    public MovieNotFoundException(Long entertainmentWorkId) {
        this("Movie with id %d doesn't exist".formatted(entertainmentWorkId));
    }

}
