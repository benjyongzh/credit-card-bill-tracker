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
        var fromAccounts = expense.getBankAccounts();
        var toCard = expense.getCreditCard();
        double splitAmount = expense.getAmount() / fromAccounts.size();

        for (var from : fromAccounts) {
            ExpenseSummary summary = summaryRepository
                    .findByUserIdAndFromAccountIdAndToCardId(user.getId(), from.getId(), toCard.getId())
                    .orElseGet(() -> {
                        ExpenseSummary s = new ExpenseSummary();
                        s.setUser(user);
                        s.setFromAccount(from);
                        s.setToCard(toCard);
                        return s;
                    });

            summary.updateExpense(splitAmount, isAdding);
            summaryRepository.save(summary);
        }
    }

    @Transactional
    public void updateFromBillPayment(User user, BillPayment payment, boolean isAdding) {
        double delta = isAdding ? payment.getAmount() : -payment.getAmount();
        UUID fromId = payment.getFromAccount().getId();

        // Handle payments to Credit Card or Bank Account
        if (payment.getToCard() != null) {
            UUID toCardId = payment.getToCard().getId();
            ExpenseSummary summary = summaryRepository
                    .findByUserIdAndFromAccountIdAndToCardId(user.getId(), fromId, toCardId)
                    .orElseGet(() -> {
                        ExpenseSummary s = new ExpenseSummary();
                        s.setUser(user);
                        BankAccount from = new BankAccount();
                        from.setId(fromId);
                        s.setFromAccount(from);
                        CreditCard toCard = new CreditCard();
                        toCard.setId(toCardId);
                        s.setToCard(toCard);
                        return s;
                    });
            summary.setTotalPaid(summary.getTotalPaid() + delta);
            summaryRepository.save(summary);

        } else if (payment.getToAccount() != null) {
            // special case: paying to another bank account — create a synthetic summary with a virtual card ID
            UUID ToAccountId = payment.getToAccount().getId();
            ExpenseSummary summary = summaryRepository
                    .findByUserIdAndFromAccountIdAndToAccountId(user.getId(), fromId, ToAccountId)
                    .orElseGet(() -> {
                        ExpenseSummary s = new ExpenseSummary();
                        s.setUser(user);
                        BankAccount from = new BankAccount();
                        from.setId(fromId);
                        s.setFromAccount(from);
                        BankAccount toAccount = new BankAccount();
                        toAccount.setId(ToAccountId);
                        s.setToAccount(toAccount);
                        return s;
                    });
            summary.setTotalPaid(summary.getTotalPaid() + delta);
            summaryRepository.save(summary);

        } else {
            return; // invalid state, skip
        }
    }
}
