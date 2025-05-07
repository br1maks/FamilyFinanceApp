package com.maximilian.familyfinance.exception.family;

public class FamilyNotFoundException extends RuntimeException {
    public FamilyNotFoundException(String message) {
        super(message);
    }
    public FamilyNotFoundException(long id) {
        super("Family with id '%s' not found".formatted(id));
    }
}
