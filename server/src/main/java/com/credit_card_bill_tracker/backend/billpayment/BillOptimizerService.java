package com.credit_card_bill_tracker.backend.billpayment;

import com.credit_card_bill_tracker.backend.bankaccount.BankAccount;
import com.credit_card_bill_tracker.backend.creditcard.CreditCard;
import com.credit_card_bill_tracker.backend.expense.Expense;
import com.credit_card_bill_tracker.backend.expense.ExpenseRepository;
import com.credit_card_bill_tracker.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BillOptimizerService {

    private final ExpenseRepository expenseRepository;
    private final BillPaymentRepository billPaymentRepository;

    public Map<String, Double> computeBills(User user) {
        List<Expense> unpaidExpenses = expenseRepository.findUnpaidExpenses(user.getId());

        Map<String, Double> billMap = new HashMap<>();

        for (Expense expense : unpaidExpenses) {
            CreditCard card = expense.getCreditCard();
            for (BankAccount account : expense.getBankAccounts()) {
                String key = account.getId() + "->" + (card != null ? card.getId() : "unknown");
                billMap.put(key, billMap.getOrDefault(key, 0.0) + expense.getAmount() / expense.getBankAccounts().size());
            }
        }

        return billMap;
    }

    public void markAllBillsComplete(User user, LocalDate date) {
        List<Expense> unpaidExpenses = expenseRepository.findUnpaidExpenses(user.getId());

        Map<String, Double> billMap = computeBills(user);

        for (Map.Entry<String, Double> entry : billMap.entrySet()) {
            String[] parts = entry.getKey().split("->");
            UUID fromAccountId = UUID.fromString(parts[0]);
            UUID toCardId = UUID.fromString(parts[1]);

            BillPayment payment = new BillPayment();
            payment.setUser(user);
            BankAccount from = new BankAccount();
            from.setId(fromAccountId);
            payment.setFromAccount(from);

            CreditCard toCard = new CreditCard();
            toCard.setId(toCardId);
            payment.setToCard(toCard);

            payment.setAmount(entry.getValue());
            payment.setDate(date);

            billPaymentRepository.save(payment);
        }
    }
}
