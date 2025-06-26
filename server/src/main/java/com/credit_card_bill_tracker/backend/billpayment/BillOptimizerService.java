package com.credit_card_bill_tracker.backend.billpayment;

import com.credit_card_bill_tracker.backend.expensesummary.ExpenseSummary;
import com.credit_card_bill_tracker.backend.expensesummary.ExpenseSummaryRepository;
import com.credit_card_bill_tracker.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BillOptimizerService {

    private final ExpenseSummaryRepository expenseSummaryRepository;

    public List<BillSuggestionDTO> computeBillSuggestions(User user) {
        List<ExpenseSummary> summaries = expenseSummaryRepository.findByUserId(user.getId());

        List<BillSuggestionDTO> suggestions = new ArrayList<>();

        for (ExpenseSummary summary : summaries) {
            double remaining = summary.getRemaining();
            if (remaining > 0) {
                BillSuggestionDTO suggestion = new BillSuggestionDTO();
                suggestion.setFrom(summary.getFromAccount().getId());
                suggestion.setAmount(remaining);
                suggestion.setTo(summary.getToId());
                suggestion.setToType(summary.getToType());
                suggestions.add(suggestion);
            }
        }
//TODO need to verify correctness and optimisation of this service method
        return suggestions;
    }
}