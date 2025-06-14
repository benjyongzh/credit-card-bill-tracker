package com.credit_card_bill_tracker.backend.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDTO {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}