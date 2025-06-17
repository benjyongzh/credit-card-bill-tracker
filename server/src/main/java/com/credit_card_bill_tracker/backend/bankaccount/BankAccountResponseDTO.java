package com.credit_card_bill_tracker.backend.bankaccount;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class BankAccountResponseDTO {
    private UUID id;
    private UUID userId;
    private String name;
    @JsonProperty("isDefault")
    private boolean isDefault;
    private UUID defaultCardId;
    private boolean changedOtherAccountsToNotDefault;//optional
}