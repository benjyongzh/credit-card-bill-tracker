package com.credit_card_bill_tracker.backend.billpayment;

import com.credit_card_bill_tracker.backend.billingcycle.BillingCycleResponseDTO;
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
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class BillPaymentController {

    private final BillPaymentService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BillPaymentResponseDTO>>> getAll(@AuthenticationPrincipal User user) {
        List<BillPaymentResponseDTO> result = service.getAll(user);
        return ApiResponseBuilder.ok(result);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BillPaymentResponseDTO>> create(@AuthenticationPrincipal User user, @RequestBody BillPaymentDTO dto) {
        BillPaymentResponseDTO result = service.create(user, dto);
        return ApiResponseBuilder.created(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BillPaymentResponseDTO>> update(@AuthenticationPrincipal User user, @PathVariable UUID id, @RequestBody BillPaymentDTO dto) {
        BillPaymentResponseDTO result = service.update(user, id, dto);
        return ApiResponseBuilder.accepted(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        service.delete(user, id);
        return ApiResponseBuilder.noContent();
    }

    @PostMapping("/complete")
    public ResponseEntity<ApiResponse<BillingCycleResponseDTO>> markAsComplete(@AuthenticationPrincipal User user) {
        BillingCycleResponseDTO result = service.markBillsComplete(user);
        return ApiResponseBuilder.accepted(result);
    }
}
