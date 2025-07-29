package com.credit_card_bill_tracker.backend.billpayment;

import org.springframework.http.ResponseEntity;
import com.credit_card_bill_tracker.backend.user.User;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.beans.PropertyEditorSupport;
import java.util.List;

@RestController
@RequestMapping("/api/bills/optimizer")
@RequiredArgsConstructor
public class BillOptimizerController {

    private final BillOptimizerService service;

    @Operation(summary = "Get bill optimization suggestions", description = "Computes optimal bill payment suggestions using the provided strategy")
    @GetMapping
    public ResponseEntity<List<BillSuggestionDTO>> getOptimized(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = OptimizationStrategy.DEFAULT) OptimizationStrategy strategy) {
        List<BillSuggestionDTO> result = service.computeBillSuggestions(user, strategy);
        return ResponseEntity.ok(result);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(OptimizationStrategy.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                setValue(OptimizationStrategy.valueOf(text.toUpperCase()));
            }
        });
    }
}
