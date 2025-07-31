package com.credit_card_bill_tracker.backend.billingcycle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillingCycleResponseDTO {
    private UUID id;
    private String label;
    private Month month;
    private LocalDate completedDate;
    private java.time.LocalDateTime updatedAt;
}