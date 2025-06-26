package com.credit_card_bill_tracker.backend.expensesummary;

import com.credit_card_bill_tracker.backend.bankaccount.BankAccount;
import com.credit_card_bill_tracker.backend.billpayment.BillPayment;
import com.credit_card_bill_tracker.backend.common.errors.UnauthorizedException;
import com.credit_card_bill_tracker.backend.creditcard.CreditCard;
import com.credit_card_bill_tracker.backend.expense.Expense;
import com.credit_card_bill_tracker.backend.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpenseSummaryService {

    private final ExpenseSummaryRepository summaryRepository;
    private final ExpenseSummaryMapper mapper;

    public List<ExpenseSummaryResponseDTO> getAllSummaries(User user, UUID userId) {
        if (user.getId() != userId) throw new UnauthorizedException("User does not have permission to view other user's summaries.");

        return summaryRepository.findByUserId(user.getId()).stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Transactional
    public void updateFromExpense(User user, Expense expense, boolean isAdding) {
        List<BankAccount> fromAccounts = expense.getBankAccounts();
        CreditCard toCard = expense.getCreditCard();
        double splitAmount = expense.getAmount() / fromAccounts.size();
        List<ExpenseSummary> expenseSummaryList = new ArrayList<>();

        for (BankAccount from : fromAccounts) {
            ExpenseSummary summary = summaryRepository
                    .findByUserIdAndFromAccountIdAndToIdAndToType(user.getId(), from.getId(), toCard.getId(), "card")
                    .orElseGet(() -> {
                        ExpenseSummary s = new ExpenseSummary();
                        s.setUser(user);
                        s.setFromAccount(from);
                        s.setToId(toCard.getId());
                        s.setToType("card");
                        return s;
                    });
            summary.updateExpense(splitAmount, isAdding);
            expenseSummaryList.add(summary);
            System.out.println("Updating summary with fromAccountId: " + from.getId());
            System.out.println("FromAccount Class: " + from.getClass());
        }
        summaryRepository.saveAll(expenseSummaryList);
    }

    @Transactional
    public void updateFromBillPayment(User user, BillPayment payment, boolean isAdding) {
        double delta = isAdding ? payment.getAmount() : -payment.getAmount();

        // Handle payments to Credit Card or Bank Account
        if (payment.getToCard() != null) {
            UUID toCardId = payment.getToCard().getId();
            ExpenseSummary summary = summaryRepository
                    .findByUserIdAndFromAccountIdAndToIdAndToType(user.getId(), payment.getFromAccount().getId(), toCardId, "card")
                    .orElseGet(() -> {
                        ExpenseSummary s = new ExpenseSummary();
                        s.setUser(user);
                        s.setFromAccount(payment.getFromAccount());
                        s.setToId(toCardId);
                        s.setToType("card");
                        return s;
                    });
            summary.updatePayment(delta, isAdding);
            summaryRepository.save(summary);

        } else if (payment.getToAccount() != null) {
            // special case: paying to another bank account — create a synthetic summary with a virtual card ID
            UUID ToAccountId = payment.getToAccount().getId();
            ExpenseSummary summary = summaryRepository
                    .findByUserIdAndFromAccountIdAndToIdAndToType(user.getId(), payment.getFromAccount().getId(), ToAccountId, "account")
                    .orElseGet(() -> {
                        ExpenseSummary s = new ExpenseSummary();
                        s.setUser(user);
                        s.setFromAccount(payment.getFromAccount());
                        s.setToId(ToAccountId);
                        s.setToType("account");
                        return s;
                    });
            summary.updatePayment(delta, isAdding);
            summaryRepository.save(summary);

        }
    }
}
