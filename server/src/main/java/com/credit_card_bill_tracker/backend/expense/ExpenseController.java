package com.credit_card_bill_tracker.backend.expense;

import com.credit_card_bill_tracker.backend.common.ApiResponse;
import com.credit_card_bill_tracker.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<List<ExpenseResponseDTO>>> get(@AuthenticationPrincipal User user, @RequestParam(required = false) UUID cardId) {
        List<ExpenseResponseDTO> result = service.getAll(user, cardId);
        ApiResponse<List<ExpenseResponseDTO>> response = new ApiResponse<>(true, "Expenses retrieved successfully", result);
        return ResponseEntity.ok(response);
    }

//    @PostMapping
//    public ResponseEntity<ApiResponse<ExpenseDTO>> create(@AuthenticationPrincipal User user, @RequestBody ExpenseDTO dto) {
//        ExpenseDTO result = service.create(user, dto);
//        ApiResponse<ExpenseDTO> response = new ApiResponse<>(true, "Expense created successfully", result);
//        return ResponseEntity.ok(response);
//    }

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseDTO>> createExpenseWithProfile(
            @AuthenticationPrincipal User user,
            @RequestParam UUID spendingProfileId,
            @RequestBody ExpenseCreateDTO dto
    ) {
        ExpenseDTO result = service.createWithSpendingProfile(user, dto, spendingProfileId);
        ApiResponse<ExpenseDTO> response = new ApiResponse<>(true, "Expense created with profile successfully", result);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseDTO>> update(@AuthenticationPrincipal User user, @PathVariable UUID id, @RequestBody ExpenseDTO dto) {
        ExpenseDTO result = service.update(user, id, dto);
        ApiResponse<ExpenseDTO> response = new ApiResponse<>(true, "Expense updated successfully", result);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        service.delete(user, id);
        ApiResponse<Void> response = new ApiResponse<>(true, "Expense deleted successfully", null);
        return ResponseEntity.ok(response);
    }
}
