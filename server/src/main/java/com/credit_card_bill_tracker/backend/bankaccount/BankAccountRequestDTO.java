package com.credit_card_bill_tracker.backend.bankaccount;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Data
public class BankAccountRequestDTO {
    @NotBlank
    @Size(max = 100)
    private String name;
    @JsonProperty("isDefault")
    private boolean isDefault;
    private UUID defaultCardId;
}