package com.credit_card_bill_tracker.backend.common.errors;

public class BadRequestException extends AppException {
    public BadRequestException(String message) {
        super(message, 400);
    }
}