package com.maximilian.familyfinance.exception.user;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(long id) {
        super("User with id '%d' not found".formatted(id));
    }
}
