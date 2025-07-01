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

        // Track the net remaining amount for every account and card.
        // Only positive remaining values represent unpaid balances. Negative
        // values mean the card was overpaid and should not influence other
        // accounts' suggestions.
        Map<UUID, Double> remainingMap = new LinkedHashMap<>();
        Map<UUID, String> typeMap = new HashMap<>();

        for (ExpenseSummary summary : summaries) {
            double remaining = summary.getRemaining();
            if (remaining <= 0) {
                // The from account already paid more than required. This
                // prepayment should be used only for future expenses on the
                // same card/account so it does not participate in the
                // optimisation step.
                continue;
            }

            UUID fromId = summary.getFromAccount().getId();
            remainingMap.merge(fromId, remaining, Double::sum);
            typeMap.putIfAbsent(fromId, "account");

            UUID toId = summary.getToId();
            remainingMap.merge(toId, -remaining, Double::sum);
            typeMap.putIfAbsent(toId, summary.getToType());
        }

        // Separate surpluses and deficits
        Map<UUID, Double> surpluses = new LinkedHashMap<>();
        Map<UUID, DeficitInfo> deficits = new LinkedHashMap<>();

        for (Map.Entry<UUID, Double> entry : remainingMap.entrySet()) {
            double amt = entry.getValue();
            if (amt > 0) {
                if ("account".equals(typeMap.get(entry.getKey()))) {
                    surpluses.put(entry.getKey(), amt);
                }
            } else if (amt < 0) {
                deficits.put(entry.getKey(), new DeficitInfo(-amt, typeMap.get(entry.getKey())));
            }
        }

        List<BillSuggestionDTO> suggestions = new ArrayList<>();

        for (Map.Entry<UUID, Double> surplusEntry : surpluses.entrySet()) {
            double available = surplusEntry.getValue();
            if (available <= 0) continue;

            for (Map.Entry<UUID, DeficitInfo> deficitEntry : deficits.entrySet()) {
                DeficitInfo info = deficitEntry.getValue();
                if (info.amount <= 0) continue;

                double payment = Math.min(available, info.amount);
                suggestions.add(new BillSuggestionDTO(
                        surplusEntry.getKey(),
                        deficitEntry.getKey(),
                        payment,
                        info.type
                ));

                available -= payment;
                info.amount -= payment;

                if (available <= 0) break;
            }
        }

        return suggestions;
    }

    private static class DeficitInfo {
        double amount; // always positive
        String type;

        DeficitInfo(double amount, String type) {
            this.amount = amount;
            this.type = type;
        }
    }
}