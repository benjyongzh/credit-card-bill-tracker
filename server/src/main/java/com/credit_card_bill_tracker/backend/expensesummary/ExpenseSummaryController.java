package com.credit_card_bill_tracker.backend.expensesummary;

import org.springframework.http.ResponseEntity;
import com.credit_card_bill_tracker.backend.user.User;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/expense-summaries")
@RequiredArgsConstructor
public class ExpenseSummaryController {

    private final ExpenseSummaryService service;

    @Operation(summary = "Get expense summaries", description = "Returns spending summaries for the provided user ID")
    @GetMapping("/{id}")
    public ResponseEntity<List<ExpenseSummaryResponseDTO>> getAllFromUserId(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        List<ExpenseSummaryResponseDTO> result = service.getAllSummaries(user, id);
        return ResponseEntity.ok(result);
    }
}
