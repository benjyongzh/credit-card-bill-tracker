package com.credit_card_bill_tracker.backend.common.errors;

import java.util.List;

public class ForbiddenException extends AppException {
    public ForbiddenException(String message, List<String> details) {
        super(message, 403, details);
    }

    public ForbiddenException(String message) {
        super(message, 403);
    }
}
