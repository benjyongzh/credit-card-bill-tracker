package com.credit_card_bill_tracker.backend.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ApiError {
    private final boolean success = false;
    private int statusCode;
    private String error;            // Error summary (e.g. "Bad Request")
    private String message;          // Developer or user-facing message
    private List<String> details;    // Optional: list of validation errors or stack hints

    public ApiError(int statusCode, String error, String message, List<String> details) {
        this.statusCode = statusCode;
        this.error = error;
        this.message = message;
        this.details = details;
    }

    public ApiError(int statusCode, String error, String message) {
        this.statusCode = statusCode;
        this.error = error;
        this.message = message;
        this.details = null;
    }
}
