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

    private final ExpenseService service;

    @GetMapping
    public List<ExpenseDTO> get(@AuthenticationPrincipal User user, @RequestParam(required = false) UUID cardId) {
        return service.getAll(user, cardId);
    }

    @PostMapping
    public ExpenseDTO create(@AuthenticationPrincipal User user, @RequestBody ExpenseDTO dto) {
        return service.create(user, dto);
    }

    @PutMapping("/{id}")
    public ExpenseDTO update(@AuthenticationPrincipal User user, @PathVariable UUID id, @RequestBody ExpenseDTO dto) {
        return service.update(user, id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        service.delete(user, id);
    }
}
