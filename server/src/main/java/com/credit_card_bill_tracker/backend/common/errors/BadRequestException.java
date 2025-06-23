package com.credit_card_bill_tracker.backend.common.errors;

import java.util.List;

public class BadRequestException extends AppException {
    public BadRequestException(String message, List<String> details) {
        super(message, 400, details);
    }

    public BadRequestException(String message) {
        super(message, 400);
    }
}