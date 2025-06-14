package com.credit_card_bill_tracker.backend.creditcard;

import lombok.Data;

import java.util.UUID;

@Data
public class CreditCardDTO {
    private UUID id;
    private String cardName;
    private String lastFourDigits;
}