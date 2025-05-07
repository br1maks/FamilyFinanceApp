package com.maximilian.familyfinance.exception.family;

public class FamilyMemberNotFoundException extends RuntimeException {
    public FamilyMemberNotFoundException(String message) {
        super(message);
    }
}
