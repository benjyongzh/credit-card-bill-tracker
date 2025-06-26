package com.credit_card_bill_tracker.backend.expensesummary;

import org.springframework.stereotype.Component;

@Component
public class ExpenseSummaryMapper {
    public ExpenseSummaryResponseDTO toResponseDto(ExpenseSummary summary) {
        ExpenseSummaryResponseDTO dto = new ExpenseSummaryResponseDTO();
        dto.setId(summary.getId());
        dto.setFromAccountId(summary.getFromAccount().getId());
        dto.setToId(summary.getToId());
        dto.setToType(summary.getToType());
        dto.setTotalExpense(summary.getTotalExpense());
        dto.setTotalPaid(summary.getTotalPaid());
        dto.setRemaining(summary.getRemaining());
        return dto;
    }
}
