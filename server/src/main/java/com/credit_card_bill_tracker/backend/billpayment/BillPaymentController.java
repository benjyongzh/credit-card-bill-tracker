package com.credit_card_bill_tracker.backend.billpayment;

import com.credit_card_bill_tracker.backend.billingcycle.BillingCycleResponseDTO;
import com.credit_card_bill_tracker.backend.common.ApiResponse;
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
        ApiResponse<List<BillPaymentResponseDTO>> response = new ApiResponse<>(true, "Bill payments retrieved successfully", result);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BillPaymentResponseDTO>> create(@AuthenticationPrincipal User user, @RequestBody BillPaymentDTO dto) {
        BillPaymentResponseDTO result = service.create(user, dto);
        ApiResponse<BillPaymentResponseDTO> response = new ApiResponse<>(true, "Bill payment created successfully", result);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BillPaymentResponseDTO>> update(@AuthenticationPrincipal User user, @PathVariable UUID id, @RequestBody BillPaymentDTO dto) {
        BillPaymentResponseDTO result = service.update(user, id, dto);
        ApiResponse<BillPaymentResponseDTO> response = new ApiResponse<>(true, "Bill payment updated successfully", result);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        service.delete(user, id);
        ApiResponse<Void> response = new ApiResponse<>(true, "Bill payment deleted successfully", null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/complete")
    public ResponseEntity<ApiResponse<BillingCycleResponseDTO>> markAsComplete(@AuthenticationPrincipal User user) {
        BillingCycleResponseDTO result = service.markBillsComplete(user);
        ApiResponse<BillingCycleResponseDTO> response = new ApiResponse<>(true, "Bills marked as complete successfully", result);
        return ResponseEntity.ok(response);
    }
}
