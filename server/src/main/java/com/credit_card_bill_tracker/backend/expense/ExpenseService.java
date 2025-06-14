package com.credit_card_bill_tracker.backend.expense;

import com.credit_card_bill_tracker.backend.bankaccount.BankAccount;
import com.credit_card_bill_tracker.backend.bankaccount.BankAccountRepository;
import com.credit_card_bill_tracker.backend.creditcard.CreditCard;
import com.credit_card_bill_tracker.backend.creditcard.CreditCardRepository;
import com.credit_card_bill_tracker.backend.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepo;
    private final CreditCardRepository creditCardRepo;
    private final BankAccountRepository bankAccountRepo;

    public List<Expense> getExpenses(UUID userId, UUID cardId) {
        return expenseRepo.findByUserIdAndCreditCardIdAndDeletedFalse(userId, cardId);
    }

    @Transactional
    public Expense createExpense(User user, ExpenseDTO dto) {
        CreditCard card = creditCardRepo.findById(dto.getCreditCardId())
                .orElseThrow(() -> new RuntimeException("Credit card not found"));

        List<BankAccount> banks = bankAccountRepo.findAllById(dto.getBankAccountIds());

        Expense expense = new Expense();
        expense.setUser(user);
        expense.setCreditCard(card);
        expense.setDate(dto.getDate());
        expense.setAmount(dto.getAmount());
        expense.setDescription(dto.getDescription());
        expense.setSharedBetween(dto.getSharedBetween());
        expense.setBankAccounts(banks);

        return expenseRepo.save(expense);
    }

    public void deleteExpense(UUID id) {
        Expense expense = expenseRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        expense.setDeleted(true);
        expenseRepo.save(expense);
    }
}
