package com.credit_card_bill_tracker.backend.creditcard;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Data
public class CreditCardDTO {
    @NotBlank
    @Size(max = 100)
    private String cardName;

    @NotBlank
    @Size(max = 4)
    private String lastFourDigits;
}