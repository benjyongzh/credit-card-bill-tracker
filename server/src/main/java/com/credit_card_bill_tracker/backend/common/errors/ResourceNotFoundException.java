package com.credit_card_bill_tracker.backend.common.errors;

public class ResourceNotFoundException extends AppException {
    public ResourceNotFoundException(String message) {
        super(message, 404);
    }
}