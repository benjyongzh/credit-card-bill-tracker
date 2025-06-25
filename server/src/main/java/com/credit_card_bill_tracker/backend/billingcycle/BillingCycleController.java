package com.credit_card_bill_tracker.backend.billingcycle;

import com.credit_card_bill_tracker.backend.billpayment.DeferredBillResponseDTO;
import com.credit_card_bill_tracker.backend.common.ApiResponse;
import com.credit_card_bill_tracker.backend.common.ApiResponseBuilder;
import com.credit_card_bill_tracker.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/billing-cycles")
@RequiredArgsConstructor
public class BillingCycleController {

    private final BillingCycleService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BillingCycleResponseDTO>>> getAll(@AuthenticationPrincipal User user) {
        List<BillingCycleResponseDTO> result = service.getAll(user);
        return ApiResponseBuilder.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BillingCycleResponseDTO>> getById(@AuthenticationPrincipal User user,
                                           @PathVariable UUID id) {
        BillingCycleResponseDTO result = service.getById(user, id);
        return ApiResponseBuilder.ok(result);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BillingCycleResponseDTO>> create(@AuthenticationPrincipal User user,
                                          @RequestBody BillingCycleDTO dto) {
        BillingCycleResponseDTO result = service.create(user, dto);
        return ApiResponseBuilder.created(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BillingCycleResponseDTO>> update(@AuthenticationPrincipal User user,
                                          @PathVariable UUID id,
                                          @RequestBody BillingCycleDTO dto) {
        BillingCycleResponseDTO result = service.update(user, id, dto);
        return ApiResponseBuilder.accepted(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        service.delete(user, id);
        return ApiResponseBuilder.noContent();
    }

    @GetMapping("/{id}/deferred-bills")
    public ResponseEntity<ApiResponse<List<DeferredBillResponseDTO>>> getDeferredBills(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        List<DeferredBillResponseDTO> result = service.getDeferredBills(user, id);
        return ApiResponseBuilder.ok(result);
    }
}
