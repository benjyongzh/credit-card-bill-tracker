package com.credit_card_bill_tracker.backend.spendingprofile;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class SpendingProfileDTO {
    private String name;
    private List<UUID> bankAccountIds;
}