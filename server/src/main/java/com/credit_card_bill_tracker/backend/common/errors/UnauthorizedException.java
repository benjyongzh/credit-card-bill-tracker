package com.credit_card_bill_tracker.backend.common.errors;

import java.util.List;

public class UnauthorizedException extends AppException {
    public UnauthorizedException(String message, List<String> details) {
        super(message, 401, details);
    }

    public UnauthorizedException(String message) {
        super(message, 401);
    }
}