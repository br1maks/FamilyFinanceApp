package com.maximilian.familyfinance.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
@Builder
public class ResponseWrapper<T> {

    private final int code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String error;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new HashMap<>();

    public static <T> ResponseWrapper<T> success(HttpStatus status, String message, T data) {
        if (!status.is2xxSuccessful()) {
            throw new IllegalArgumentException("Status code must be 2xx");
        }

        return ResponseWrapper.<T>builder()
                .code(status.value())
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ResponseWrapper<T> success(HttpStatus status, String message) {
        return success(status, message, null);
    }

    public static ResponseWrapper<Void> error(HttpStatus status, String message) {
        if (status.is2xxSuccessful()) {
            throw new IllegalArgumentException("Status code must not be 2xx");
        }

        return ResponseWrapper.<Void>builder()
                .code(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .build();
    }

    public static <T> ResponseWrapper<T> error(HttpStatus status, String message, T data) {
        if (status.is2xxSuccessful()) {
            throw new IllegalArgumentException("Status code must not be 2xx");
        }

        return ResponseWrapper.<T>builder()
                .code(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .data(data)
                .build();
    }

    public ResponseWrapper<T> addField(String key, Object value) {
        this.additionalFields.put(key, value);
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalFields() {
        return additionalFields;
    }
}