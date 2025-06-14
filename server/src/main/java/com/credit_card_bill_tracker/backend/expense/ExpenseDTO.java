package com.credit_card_bill_tracker.backend.expense;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class ExpenseDTO {
    private UUID creditCardId;
    private LocalDate date;
    private double amount;
    private String description;
    private int sharedBetween;
    private List<UUID> bankAccountIds;
}