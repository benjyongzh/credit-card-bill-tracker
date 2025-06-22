package com.credit_card_bill_tracker.backend.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {
    private String error;            // Error summary (e.g. "Bad Request")
    private String message;          // Developer or user-facing message
    private List<String> details;    // Optional: list of validation errors or stack hints
}
