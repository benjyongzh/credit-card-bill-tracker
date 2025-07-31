package com.credit_card_bill_tracker.backend.billpayment;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.credit_card_bill_tracker.backend.user.User;
import com.credit_card_bill_tracker.backend.billpayment.BillPaymentRequestDTO;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class BillPaymentController {

    private final BillPaymentService service;

    @Operation(summary = "Get bill payments", description = "Returns a list of scheduled bill payments")
    @GetMapping
    public ResponseEntity<List<BillPaymentResponseDTO>> getAll(@AuthenticationPrincipal User user) {
        List<BillPaymentResponseDTO> result = service.getAll(user);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Create bill payment", description = "Registers a new payment for a bill")
    @PostMapping
    public ResponseEntity<BillPaymentResponseDTO> create(@AuthenticationPrincipal User user, @Valid @RequestBody BillPaymentRequestDTO dto) {
        BillPaymentResponseDTO result = service.create(user, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Update bill payment", description = "Updates a registered bill payment")
    @PutMapping("/{id}")
    public ResponseEntity<BillPaymentResponseDTO> update(@AuthenticationPrincipal User user, @PathVariable UUID id, @Valid @RequestBody BillPaymentRequestDTO dto) {
        BillPaymentResponseDTO result = service.update(user, id, dto);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(result);
    }

    @Operation(summary = "Delete bill payment", description = "Removes the specified bill payment")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        service.delete(user, id);
        return ResponseEntity.noContent().build();
    }

}
