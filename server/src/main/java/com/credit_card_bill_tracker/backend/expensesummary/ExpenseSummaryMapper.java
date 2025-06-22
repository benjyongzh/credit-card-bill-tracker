package com.credit_card_bill_tracker.backend.expensesummary;

import org.springframework.stereotype.Component;

@Component
public class ExpenseSummaryMapper {
    public ExpenseSummaryResponseDTO toResponseDto(ExpenseSummary summary) {
        ExpenseSummaryResponseDTO dto = new ExpenseSummaryResponseDTO();
        dto.setFromAccountId(summary.getId());
        dto.setFromAccountId(summary.getFromAccount().getId());
        dto.setToCardId(summary.getToCard().getId());
        dto.setTotalExpense(summary.getTotalExpense());
        dto.setTotalPaid(summary.getTotalPaid());
        dto.setRemaining(summary.getRemaining());
        return dto;
    }
}
