package com.credit_card_bill_tracker.backend.billingcycle;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class BillingCycleRequestDTO {
    @NotBlank
    @Size(max = 100)
    private String label;
    private LocalDate completedDate;
    private List<UUID> billPaymentIds;
}