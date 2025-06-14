package com.credit_card_bill_tracker.backend.billpayment;

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
    public List<BillPayment> getAll(@AuthenticationPrincipal User user) {
        return service.getAllByUser(user.getId());
    }

    @PostMapping
    public BillPayment create(@AuthenticationPrincipal User user, @RequestBody BillPaymentDTO dto) {
        return service.create(user, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
