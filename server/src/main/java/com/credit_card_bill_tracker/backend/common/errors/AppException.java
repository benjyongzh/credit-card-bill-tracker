package com.credit_card_bill_tracker.backend.common.errors;

import lombok.Getter;

import java.util.List;

@Getter
public abstract class AppException extends RuntimeException {
    private final int statusCode;

    private final List<String> details;

    public AppException(String message, int statusCode, List<String> details) {
        super(message);
        this.statusCode = statusCode;
        this.details = details;
    }

    // Constructor without details (details will be null)
    public AppException(String message, int statusCode) {
        this(message, statusCode, null);
    }

}
