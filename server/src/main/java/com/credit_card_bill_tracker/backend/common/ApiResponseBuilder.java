package com.credit_card_bill_tracker.backend.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponseBuilder {

    public static <T> ResponseEntity<ApiResponse<T>> ok(T data) {
        return build(data, "Success", HttpStatus.OK);
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(T data) {
        return build(data, "Created", HttpStatus.CREATED);
    }

    public static <T> ResponseEntity<ApiResponse<T>> accepted(T data) {
        return build(data, "Accepted", HttpStatus.ACCEPTED);
    }

    public static <T> ResponseEntity<ApiResponse<T>> noContent() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    public static <T> ResponseEntity<ApiResponse<T>> build(T data, String message, HttpStatus status) {
        ApiResponse<T> response = new ApiResponse<>(status.value(), message, data);
        return ResponseEntity.status(status).body(response);
    }
}