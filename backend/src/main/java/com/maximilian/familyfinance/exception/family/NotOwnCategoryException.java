package com.maximilian.familyfinance.exception.family;

public class NotOwnCategoryException extends RuntimeException {
    public NotOwnCategoryException(String message) {
        super(message);
    }
}
