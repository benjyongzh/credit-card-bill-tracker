package com.credit_card_bill_tracker.backend.expensesummary;

import com.credit_card_bill_tracker.backend.common.ApiResponse;
import com.credit_card_bill_tracker.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/expense-summaries")
@RequiredArgsConstructor
public class ExpenseSummaryController {

    private final ExpenseSummaryService service;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<List<ExpenseSummaryResponseDTO>>> getAllFromUserId(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        List<ExpenseSummaryResponseDTO> result = service.getAllSummaries(user, id);
        ApiResponse<List<ExpenseSummaryResponseDTO>> response = new ApiResponse<>(true, "Expense Summaries retrieved successfully", result);
        return ResponseEntity.ok(response);
    }
}
