package com.maximilian.familyfinance.exception.family;

public class FamilyAlreadyJoinedException extends RuntimeException {
    public FamilyAlreadyJoinedException(String message) {
        super(message);
    }
}
