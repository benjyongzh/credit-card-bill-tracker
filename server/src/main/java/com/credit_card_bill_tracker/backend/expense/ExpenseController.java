package com.credit_card_bill_tracker.backend.expense;

import com.credit_card_bill_tracker.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping("/{cardId}")
    public List<Expense> getExpenses(@AuthenticationPrincipal User user, @PathVariable UUID cardId) {
        return expenseService.getExpenses(user.getId(), cardId);
    }

    @PostMapping
    public Expense create(@AuthenticationPrincipal User user, @RequestBody ExpenseDTO dto) {
        return expenseService.createExpense(user, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        expenseService.deleteExpense(id);
    }
}
