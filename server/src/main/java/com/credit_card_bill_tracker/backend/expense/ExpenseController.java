package com.credit_card_bill_tracker.backend.expense;

import com.credit_card_bill_tracker.backend.common.ApiResponse;
import com.credit_card_bill_tracker.backend.common.ApiResponseBuilder;
import com.credit_card_bill_tracker.backend.user.User;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Get expenses", description = "Returns expenses for the user, optionally filtered by credit card")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ExpenseResponseDTO>>> get(@AuthenticationPrincipal User user, @RequestParam(required = false) UUID cardId) {
        List<ExpenseResponseDTO> result = service.getAll(user, cardId);
        return ApiResponseBuilder.ok(result);
    }

//    @PostMapping
//    public ResponseEntity<ApiResponse<ExpenseDTO>> create(@AuthenticationPrincipal User user, @RequestBody ExpenseDTO dto) {
//        ExpenseDTO result = service.create(user, dto);
//        ApiResponse<ExpenseDTO> response = new ApiResponse<>(true, "Expense created successfully", result);
//        return ResponseEntity.ok(response);
//    }

    @Operation(summary = "Create expense", description = "Adds an expense and updates the related spending profile")
    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponseDTO>> createExpenseWithProfile(
            @AuthenticationPrincipal User user,
            @RequestParam UUID spendingProfileId,
            @RequestBody ExpenseCreateDTO dto
    ) {
        ExpenseResponseDTO result = service.createWithSpendingProfile(user, dto, spendingProfileId);
        return ApiResponseBuilder.created(result);
    }

    @Operation(summary = "Update expense", description = "Modifies an existing expense")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponseDTO>> update(@AuthenticationPrincipal User user, @PathVariable UUID id, @RequestBody ExpenseDTO dto) {
        ExpenseResponseDTO result = service.update(user, id, dto);
        return ApiResponseBuilder.accepted(result);
    }

    @Operation(summary = "Delete expense", description = "Removes the specified expense record")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        service.delete(user, id);
        return ApiResponseBuilder.noContent();
    }
}
