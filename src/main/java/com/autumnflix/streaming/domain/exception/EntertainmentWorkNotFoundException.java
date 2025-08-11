package com.autumnflix.streaming.domain.exception;

public class EntertainmentWorkNotFoundException extends EntityNotFoundException{
    public EntertainmentWorkNotFoundException(String message) {
        super(message);
    }

    public EntertainmentWorkNotFoundException(Long entertainmentWorkId) {
        this("Entertainment Work with id %d doesn't exist".formatted(entertainmentWorkId));
    }
}
