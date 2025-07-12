package com.credit_card_bill_tracker.backend.expense;

import lombok.Data;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;
import java.time.LocalDate;

@Data
public class ExpenseRequestDTO {
    private UUID creditCardId;
    private LocalDate date;
    private double amount;
    @Size(max = 255)
    private String description;
    private List<UUID> bankAccountIds;
}