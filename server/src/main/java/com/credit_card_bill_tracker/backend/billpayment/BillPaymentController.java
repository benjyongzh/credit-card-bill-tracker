package com.credit_card_bill_tracker.backend.billpayment;

import com.credit_card_bill_tracker.backend.billingcycle.BillingCycleResponseDTO;
import com.credit_card_bill_tracker.backend.user.User;
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

    @GetMapping
    public List<BillPaymentResponseDTO> getAll(@AuthenticationPrincipal User user) {
        return service.getAll(user);
    }

    @PostMapping
    public BillPaymentResponseDTO create(@AuthenticationPrincipal User user, @RequestBody BillPaymentDTO dto) {
        return service.create(user, dto);
    }

    @PutMapping("/{id}")
    public BillPaymentResponseDTO update(@AuthenticationPrincipal User user, @PathVariable UUID id, @RequestBody BillPaymentDTO dto) {
        return service.update(user, id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        service.delete(user, id);
    }

    @PostMapping("/complete")
    public BillingCycleResponseDTO markAsComplete(@AuthenticationPrincipal User user) {
        return service.markBillsComplete(user);
    }
}
