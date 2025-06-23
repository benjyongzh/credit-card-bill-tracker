package com.credit_card_bill_tracker.backend.common.errors;

import java.util.List;

public class ResourceNotFoundException extends AppException {
    public ResourceNotFoundException(String message, List<String> details) {
        super(message, 404, details);
    }

    public ResourceNotFoundException(String message) {
        super(message, 404);
    }
}