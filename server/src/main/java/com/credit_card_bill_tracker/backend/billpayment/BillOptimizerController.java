package com.credit_card_bill_tracker.backend.billpayment;

import com.credit_card_bill_tracker.backend.common.ApiResponse;
import com.credit_card_bill_tracker.backend.common.ApiResponseBuilder;
import com.credit_card_bill_tracker.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bills/optimizer")
@RequiredArgsConstructor
public class BillOptimizerController {

    private final BillOptimizerService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BillSuggestionDTO>>> getOptimized(@AuthenticationPrincipal User user) {
        List<BillSuggestionDTO> result = service.computeBillSuggestions(user);
        return ApiResponseBuilder.ok(result);
    }
}
