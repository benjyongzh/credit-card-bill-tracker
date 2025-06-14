package com.credit_card_bill_tracker.backend.billpayment;

import com.credit_card_bill_tracker.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bill-payments")
@RequiredArgsConstructor
public class BillPaymentController {

    private final BillPaymentService service;

    @GetMapping
    public List<BillPaymentDTO> getAll(@AuthenticationPrincipal User user) {
        return service.getAll(user);
    }

    @PostMapping
    public BillPaymentDTO create(@AuthenticationPrincipal User user, @RequestBody BillPaymentDTO dto) {
        return service.create(user, dto);
    }

    @PutMapping("/{id}")
    public BillPaymentDTO update(@AuthenticationPrincipal User user, @PathVariable UUID id, @RequestBody BillPaymentDTO dto) {
        return service.update(user, id, dto);
    }
}
