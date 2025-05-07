package com.maximilian.familyfinance.exception.family;

public class OwnerRoleDowngradeNotAllowedException extends RuntimeException {
    public OwnerRoleDowngradeNotAllowedException(String message) {
        super(message);
    }
}
