package com.credit_card_bill_tracker.backend.billingcycle;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class BillingCycleDTO {
    private String label;
    private LocalDate completedDate;
    private List<UUID> billPaymentIds;
}