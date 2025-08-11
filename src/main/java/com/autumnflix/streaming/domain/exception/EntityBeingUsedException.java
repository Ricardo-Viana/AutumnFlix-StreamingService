package com.autumnflix.streaming.domain.exception;

public class EntityBeingUsedException extends BusinessException{
    public EntityBeingUsedException(String message) {
        super(message);
    }

    public EntityBeingUsedException(String entity, Long id) {
        this("%s with id %d is being used and can't be removed".formatted(entity, id));
    }

    public EntityBeingUsedException(String entity, Integer number) {
        this("%s %d is being used and can't be removed".formatted(entity, number));
    }
}
