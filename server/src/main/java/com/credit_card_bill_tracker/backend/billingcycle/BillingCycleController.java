package com.credit_card_bill_tracker.backend.billingcycle;

import com.credit_card_bill_tracker.backend.billpayment.DeferredBill;
import com.credit_card_bill_tracker.backend.billpayment.DeferredBillResponseDTO;
import com.credit_card_bill_tracker.backend.common.ApiResponse;
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
        ApiResponse<List<BillingCycleResponseDTO>> response = new ApiResponse<>(true, "Billing cycles retrieved successfully", result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BillingCycleResponseDTO>> getById(@AuthenticationPrincipal User user,
                                           @PathVariable UUID id) {
        BillingCycleResponseDTO result = service.getById(user, id);
        ApiResponse<BillingCycleResponseDTO> response = new ApiResponse<>(true, "Billing cycle retrieved successfully", result);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BillingCycleResponseDTO>> create(@AuthenticationPrincipal User user,
                                          @RequestBody BillingCycleDTO dto) {
        BillingCycleResponseDTO result = service.create(user, dto);
        ApiResponse<BillingCycleResponseDTO> response = new ApiResponse<>(true, "Billing cycle created successfully", result);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BillingCycleResponseDTO>> update(@AuthenticationPrincipal User user,
                                          @PathVariable UUID id,
                                          @RequestBody BillingCycleDTO dto) {
        BillingCycleResponseDTO result = service.update(user, id, dto);
        ApiResponse<BillingCycleResponseDTO> response = new ApiResponse<>(true, "Billing cycle updated successfully", result);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        service.delete(user, id);
        ApiResponse<Void> response = new ApiResponse<>(true, "Billing cycle deleted successfully", null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/deferred-bills")
    public ResponseEntity<ApiResponse<List<DeferredBillResponseDTO>>> getDeferredBills(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        List<DeferredBillResponseDTO> result = service.getDeferredBills(user, id);
        ApiResponse<List<DeferredBillResponseDTO>> response = new ApiResponse<>(true, "Deferred bills retrieved successfully", result);
        return ResponseEntity.ok(response);
    }
}
