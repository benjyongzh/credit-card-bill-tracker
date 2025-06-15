package com.credit_card_bill_tracker.backend.expense;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class ExpenseCreateDTO {
    private UUID creditCardId;
    private LocalDate date;
    private double amount;
    private String description;
}