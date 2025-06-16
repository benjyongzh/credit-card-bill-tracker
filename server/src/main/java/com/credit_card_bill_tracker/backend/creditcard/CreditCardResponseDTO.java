package com.credit_card_bill_tracker.backend.creditcard;

import lombok.Data;

import java.util.UUID;

@Data
public class CreditCardResponseDTO {
    private UUID id;
    private UUID userId;
    private String cardName;
    private String lastFourDigits;
}