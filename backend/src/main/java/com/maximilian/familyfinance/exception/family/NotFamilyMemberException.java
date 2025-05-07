package com.maximilian.familyfinance.exception.family;

public class NotFamilyMemberException extends RuntimeException {
    public NotFamilyMemberException(String message) {
        super(message);
    }
}
