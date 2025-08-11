package com.autumnflix.streaming.domain.exception;

public class UserNotFoundException extends EntityNotFoundException{
    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(Long userId) {
        this(String.format("User with id %d doesn't exist", userId));
    }

}
