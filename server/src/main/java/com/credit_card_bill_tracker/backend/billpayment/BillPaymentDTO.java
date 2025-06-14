package com.credit_card_bill_tracker.backend.billpayment;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class BillPaymentDTO {
    private UUID id;
    private UUID fromAccountId;
    private UUID toCardId;
    private UUID toAccountId;
    private double amount;
    private LocalDate date;
    private boolean completed;
}