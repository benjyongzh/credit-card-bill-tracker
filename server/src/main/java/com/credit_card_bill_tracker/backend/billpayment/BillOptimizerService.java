package com.credit_card_bill_tracker.backend.billpayment;

import com.credit_card_bill_tracker.backend.expensesummary.ExpenseSummary;
import com.credit_card_bill_tracker.backend.expensesummary.ExpenseSummaryRepository;
import com.credit_card_bill_tracker.backend.expensesummary.TargetType;
import com.credit_card_bill_tracker.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BillOptimizerService {

    private final ExpenseSummaryRepository expenseSummaryRepository;

    public List<BillSuggestionDTO> computeBillSuggestions(User user, OptimizationStrategy strategy) {
        List<ExpenseSummary> summaries = expenseSummaryRepository.findByUserId(user.getId());

        // Track the net remaining amount for every account and card.
        // Only positive remaining values represent unpaid balances. Negative
        // values mean the card was overpaid and should not influence other
        // accounts' suggestions.
        Map<UUID, Double> remainingMap = new LinkedHashMap<>();
        Map<UUID, TargetType> typeMap = new HashMap<>();

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
            typeMap.putIfAbsent(fromId, TargetType.ACCOUNT);

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
                if (TargetType.ACCOUNT.equals(typeMap.get(entry.getKey()))) {
                    surpluses.put(entry.getKey(), amt);
                }
            } else if (amt < 0) {
                deficits.put(entry.getKey(), new DeficitInfo(-amt, typeMap.get(entry.getKey())));
            }
        }

        if (strategy == OptimizationStrategy.MINIMIZE_ACCOUNTS) {
            return minimizeAccounts(surpluses, deficits);
        } else {
            return minimizeTransactions(surpluses, deficits);
        }
    }

    private List<BillSuggestionDTO> minimizeTransactions(Map<UUID, Double> surpluses, Map<UUID, DeficitInfo> deficits) {
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

    private List<BillSuggestionDTO> minimizeAccounts(Map<UUID, Double> surpluses, Map<UUID, DeficitInfo> deficits) {
        List<BillSuggestionDTO> suggestions = new ArrayList<>();
        double totalDeficit = deficits.values().stream().mapToDouble(d -> d.amount).sum();

        List<Map.Entry<UUID, Double>> sortedSurpluses = surpluses.entrySet().stream()
                .sorted(Map.Entry.<UUID, Double>comparingByValue().reversed())
                .toList();

        for (Map.Entry<UUID, Double> surplusEntry : sortedSurpluses) {
            if (totalDeficit <= 0) break;

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
                totalDeficit -=payment;
            }
        }

        return suggestions;
    }

    private static class DeficitInfo {
        double amount; // always positive
        TargetType type;

        DeficitInfo(double amount, TargetType type) {
            this.amount = amount;
            this.type = type;
        }
    }
}