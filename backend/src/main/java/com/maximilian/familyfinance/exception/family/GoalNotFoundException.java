package com.maximilian.familyfinance.exception.family;

public class GoalNotFoundException extends RuntimeException {
    public GoalNotFoundException(String message) {
        super(message);
    }
    public GoalNotFoundException(long goalId) {
        super("Goal with id '%s' not found".formatted(goalId));
    }
}
