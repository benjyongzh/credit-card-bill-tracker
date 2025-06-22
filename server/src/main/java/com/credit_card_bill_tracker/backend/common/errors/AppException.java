package com.credit_card_bill_tracker.backend.common.errors;

import lombok.Getter;

@Getter
public abstract class AppException extends RuntimeException {
    private final int statusCode;

    public AppException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

}
