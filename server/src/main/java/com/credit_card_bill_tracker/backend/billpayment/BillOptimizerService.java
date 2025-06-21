package com.credit_card_bill_tracker.backend.billpayment;

import com.credit_card_bill_tracker.backend.bankaccount.BankAccount;
import com.credit_card_bill_tracker.backend.creditcard.CreditCard;
import com.credit_card_bill_tracker.backend.expense.Expense;
import com.credit_card_bill_tracker.backend.expense.ExpenseRepository;
import com.credit_card_bill_tracker.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BillOptimizerService {

    private final ExpenseRepository expenseRepository;
    private final BillPaymentRepository billPaymentRepository;

    public List<BillSuggestionDTO> computeBillSuggestions(User user) {
//        TODO still giving old expenses
        List<Expense> unpaidExpenses = expenseRepository.findUnpaidExpenses(user.getId());

        // Accumulate totals from bank accounts to credit cards based on expenses
        Map<String, BillSuggestionDTO> suggestionMap = new HashMap<>();

        for (Expense expense : unpaidExpenses) {
            CreditCard card = expense.getCreditCard();
            List<BankAccount> accounts = expense.getBankAccounts();
            double splitAmount = expense.getAmount() / accounts.size();

            for (BankAccount account : accounts) {
                String key = account.getId() + ":card:" + card.getId();
                suggestionMap.merge(
                        key,
                        new BillSuggestionDTO(account.getId(), card.getId(), splitAmount, "card"),
                        (oldVal, newVal) -> {
                            oldVal.setAmount(oldVal.getAmount() + newVal.getAmount());
                            return oldVal;
                        }
                );
            }
        }

        // Subtract any in-progress BillPayments
        List<BillPayment> inProgressPayments = billPaymentRepository.findByUserIdAndCompletedFalse(user.getId());

        for (BillPayment bp : inProgressPayments) {
            UUID fromId = bp.getFromAccount().getId();
            UUID toId;
            String toType;

            if (bp.getToCard() != null) {
                toId = bp.getToCard().getId();
                toType = "card";
            } else if (bp.getToAccount() != null) {
                toId = bp.getToAccount().getId();
                toType = "account";
            } else {
                continue; // invalid state
            }

            String key = fromId + ":" + toType + ":" + toId;

            if (suggestionMap.containsKey(key)) {
                BillSuggestionDTO suggestion = suggestionMap.get(key);
                suggestion.setAmount(suggestion.getAmount() - bp.getAmount());
                if (suggestion.getAmount() <= 0) {
                    suggestionMap.remove(key);
                }
            }
        }

        return new ArrayList<>(suggestionMap.values());
    }
}