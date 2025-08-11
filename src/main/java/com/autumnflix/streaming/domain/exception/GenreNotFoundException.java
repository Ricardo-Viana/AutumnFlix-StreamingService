package com.autumnflix.streaming.domain.exception;

public class GenreNotFoundException extends EntityNotFoundException {

    public GenreNotFoundException(String message) {
        super(message);
    }

    public GenreNotFoundException(Long genreId) {
        this(String.format("Genre with id %d doesn't exist", genreId));
    }
}
