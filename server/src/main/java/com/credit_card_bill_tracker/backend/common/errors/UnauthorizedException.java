package com.credit_card_bill_tracker.backend.common.errors;

public class UnauthorizedException extends AppException {
    public UnauthorizedException(String message) {
        super(message, 401);
    }
}