package com.credit_card_bill_tracker.backend.common;

import lombok.*;

@Data
@NoArgsConstructor
@Getter
@Setter
public class ApiResponse<T> {
    private final boolean success = true;
    private int statusCode;
    private String message;
    private T data;

    public ApiResponse(int statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }
}