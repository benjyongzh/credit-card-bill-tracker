package com.credit_card_bill_tracker.backend.common.errors;

import java.util.List;

public class ConflictException extends AppException {
    public ConflictException(String message, List<String> details) {
        super(message, 409, details);
    }

    public ConflictException(String message) {
        super(message, 409);
    }
}
