package com.maximilian.familyfinance.exception.auth;

public class UserNotAuthenticatedException extends RuntimeException {
    public UserNotAuthenticatedException(String message) {
        super(message);
    }
    public UserNotAuthenticatedException() {
        super("User not authenticated. If you think this is a bug, please contact us in tech support");
    }
}
