package com.credit_card_bill_tracker.backend.expensesummary;

import lombok.Data;

import java.util.UUID;

@Data
public class ExpenseSummaryResponseDTO {
    private UUID id;
    private UUID fromAccountId;
    private UUID toId;
    private String toType;
    private double totalExpense;
    private double totalPaid;
    private double remaining;
}