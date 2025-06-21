package com.credit_card_bill_tracker.backend.billpayment;

import lombok.Data;

import java.util.UUID;

@Data
public class DeferredBillResponseDTO {
    private UUID id;
    private UUID userId;
    private UUID fromAccountId;
    private UUID toCardId;
    private UUID toAccountId;
    private double amount;
    private UUID billingCycleId;
}