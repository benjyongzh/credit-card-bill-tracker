package com.credit_card_bill_tracker.backend.spendingprofile;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

@Data
public class SpendingProfileDTO {
    @NotBlank
    @Size(max = 100)
    private String name;
    private List<UUID> bankAccountIds;
}