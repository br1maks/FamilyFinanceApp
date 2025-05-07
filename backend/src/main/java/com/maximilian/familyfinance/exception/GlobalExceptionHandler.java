package com.maximilian.familyfinance.exception;

import com.maximilian.familyfinance.dto.ResponseWrapper;
import com.maximilian.familyfinance.exception.budget.BudgetNotFoundException;
import com.maximilian.familyfinance.exception.family.*;
import com.maximilian.familyfinance.exception.user.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseWrapper<Map<String, Object>>> handle(MethodArgumentNotValidException e) {
        Map<String, Object> errors = new HashMap<>();
        e.getBindingResult().getAllErrors()
                .forEach(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
                });

        return ResponseEntity
                .badRequest()
                .body(ResponseWrapper.error(HttpStatus.BAD_REQUEST, "Validation error", errors));
    }

    @ExceptionHandler({
            FamilyNotFoundException.class,
            BudgetNotFoundException.class,
            CategoryNotFoundException.class,
            FamilyMemberNotFoundException.class
    })
    public ResponseEntity<ResponseWrapper<Void>> handle(FamilyNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.error(
                HttpStatus.NOT_FOUND,
                e.getMessage()
        ));
    }

    @ExceptionHandler({
            AccessDeniedException.class,
            NotFamilyMemberException.class,
            NotOwnCategoryException.class
    })
    public ResponseEntity<ResponseWrapper<Void>> handle(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseWrapper.error(
                HttpStatus.FORBIDDEN,
                e.getMessage()
        ));
    }

    @ExceptionHandler({
            FamilyAlreadyJoinedException.class,
            CannotKickSelfException.class,
            SingleOwnerException.class,
            UserNotFoundException.class,
            InvalidInviteCodeException.class,
            OwnerCannotLeaveFamilyException.class,
            SingleOwnerException.class
    })
    public ResponseEntity<ResponseWrapper<Void>> handle(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.error(
                HttpStatus.BAD_REQUEST,
                e.getMessage()
        ));
    }
}
