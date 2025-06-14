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

    public Map<String, Double> computeBills(User user) {
        List<Expense> unpaidExpenses = expenseRepository.findUnpaidExpenses(user.getId());

        Map<String, Double> billMap = new HashMap<>();

        // Initial calculation
        for (Expense expense : unpaidExpenses) {
            CreditCard card = expense.getCreditCard();
            for (BankAccount account : expense.getBankAccounts()) {
                String key = account.getId() + "->" + (card != null ? card.getId() : "unknown");
                billMap.put(key, billMap.getOrDefault(key, 0.0) + expense.getAmount() / expense.getBankAccounts().size());
            }
        }

        // Subtract uncompleted payments
        List<BillPayment> inProgressPayments = billPaymentRepository.findByUserIdAndCompletedFalse(user.getId());
        for (BillPayment bp : inProgressPayments) {
            if (bp.getFromAccount() != null && bp.getToCard() != null) {
                String key = bp.getFromAccount().getId() + "->" + bp.getToCard().getId();
                billMap.put(key, billMap.getOrDefault(key, 0.0) - bp.getAmount());
                if (billMap.get(key) <= 0) {
                    billMap.remove(key);
                }
            }
        }

        return billMap;
    }

    public void markAllBillsComplete(User user) {
        List<BillPayment> inProgress = billPaymentRepository.findByUserIdAndCompletedFalse(user.getId());
        for (BillPayment bp : inProgress) {
            bp.setCompleted(true);
        }
        billPaymentRepository.saveAll(inProgress);
    }
}