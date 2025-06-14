package com.credit_card_bill_tracker.backend.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserResponseDTO {
    private String username;
    private String email;
    private String role;
}