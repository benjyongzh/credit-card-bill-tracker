package com.credit_card_bill_tracker.backend.bankaccount;

import lombok.Data;

import java.util.UUID;

@Data
public class BankAccountResponseDTO {
    private UUID id;
    private UUID userId;
    private String name;
    private boolean isDefault;
    private UUID defaultCardId;
}