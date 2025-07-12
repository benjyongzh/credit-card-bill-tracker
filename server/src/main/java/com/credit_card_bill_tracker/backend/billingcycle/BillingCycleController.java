package com.credit_card_bill_tracker.backend.billingcycle;

import com.credit_card_bill_tracker.backend.billpayment.DeferredBillResponseDTO;
import com.credit_card_bill_tracker.backend.common.ApiResponse;
import com.credit_card_bill_tracker.backend.common.ApiResponseBuilder;
import com.credit_card_bill_tracker.backend.user.User;
import com.credit_card_bill_tracker.backend.billingcycle.BillingCycleRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Get billing cycles", description = "Lists all billing cycles for the authenticated user")
    @GetMapping
    public ResponseEntity<ApiResponse<List<BillingCycleResponseDTO>>> getAll(@AuthenticationPrincipal User user) {
        List<BillingCycleResponseDTO> result = service.getAll(user);
        return ApiResponseBuilder.ok(result);
    }

    @Operation(summary = "Get billing cycle by id", description = "Retrieves details of a billing cycle")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BillingCycleResponseDTO>> getById(@AuthenticationPrincipal User user,
                                           @PathVariable UUID id) {
        BillingCycleResponseDTO result = service.getById(user, id);
        return ApiResponseBuilder.ok(result);
    }

    @Operation(summary = "Create billing cycle", description = "Creates a new billing cycle for the user")
    @PostMapping
    public ResponseEntity<ApiResponse<BillingCycleResponseDTO>> create(@AuthenticationPrincipal User user,
                                          @RequestBody BillingCycleRequestDTO dto) {
        BillingCycleResponseDTO result = service.create(user, dto);
        return ApiResponseBuilder.created(result);
    }

    @Operation(summary = "Update billing cycle", description = "Updates an existing billing cycle")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BillingCycleResponseDTO>> update(@AuthenticationPrincipal User user,
                                          @PathVariable UUID id,
                                          @RequestBody BillingCycleRequestDTO dto) {
        BillingCycleResponseDTO result = service.update(user, id, dto);
        return ApiResponseBuilder.accepted(result);
    }

    @Operation(summary = "Delete billing cycle", description = "Deletes the specified billing cycle")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        service.delete(user, id);
        return ApiResponseBuilder.noContent();
    }

    @Operation(summary = "Get deferred bills", description = "Returns the deferred bills for a billing cycle")
    @GetMapping("/{id}/deferred-bills")
    public ResponseEntity<ApiResponse<List<DeferredBillResponseDTO>>> getDeferredBills(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        List<DeferredBillResponseDTO> result = service.getDeferredBills(user, id);
        return ApiResponseBuilder.ok(result);
    }
}
