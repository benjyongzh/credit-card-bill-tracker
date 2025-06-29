package com.credit_card_bill_tracker.backend.expensesummary;

import lombok.Data;

import java.util.UUID;

import com.credit_card_bill_tracker.backend.expensesummary.TargetType;

/**
 * DTO representing aggregated expense information for an account-card pair.
 */

@Data
public class ExpenseSummaryResponseDTO {
    private UUID id;
    private UUID fromAccountId;
    private UUID toId;
    private TargetType toType;
    private double totalExpense;
    private double totalPaid;
    private double remaining;
}