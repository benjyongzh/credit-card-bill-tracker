package com.credit_card_bill_tracker.backend.billingcycle;

import com.credit_card_bill_tracker.backend.billpayment.DeferredBillResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.credit_card_bill_tracker.backend.user.User;
import com.credit_card_bill_tracker.backend.billingcycle.BillingCycleRequestDTO;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<BillingCycleResponseDTO>> getAll(@AuthenticationPrincipal User user) {
        List<BillingCycleResponseDTO> result = service.getAll(user);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get billing cycle by id", description = "Retrieves details of a billing cycle")
    @GetMapping("/{id}")
    public ResponseEntity<BillingCycleResponseDTO> getById(@AuthenticationPrincipal User user,
                                           @PathVariable UUID id) {
        BillingCycleResponseDTO result = service.getById(user, id);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Create billing cycle", description = "Creates a new billing cycle for the user")
    @PostMapping
    public ResponseEntity<BillingCycleResponseDTO> create(@AuthenticationPrincipal User user,
                                          @Valid @RequestBody BillingCycleRequestDTO dto) {
        BillingCycleResponseDTO result = service.create(user, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Update billing cycle", description = "Updates an existing billing cycle")
    @PutMapping("/{id}")
    public ResponseEntity<BillingCycleResponseDTO> update(@AuthenticationPrincipal User user,
                                          @PathVariable UUID id,
                                          @Valid @RequestBody BillingCycleRequestDTO dto) {
        BillingCycleResponseDTO result = service.update(user, id, dto);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(result);
    }

    @Operation(summary = "Complete billing cycle", description = "Marks the cycle as completed")
    @PostMapping("/{id}/complete")
    public ResponseEntity<BillingCycleResponseDTO> complete(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        BillingCycleResponseDTO result = service.complete(user, id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(result);
    }

    @Operation(summary = "Delete billing cycle", description = "Deletes the specified billing cycle")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        service.delete(user, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get deferred bills", description = "Returns the deferred bills for a billing cycle")
    @GetMapping("/{id}/deferred-bills")
    public ResponseEntity<List<DeferredBillResponseDTO>> getDeferredBills(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        List<DeferredBillResponseDTO> result = service.getDeferredBills(user, id);
        return ResponseEntity.ok(result);
    }
}
