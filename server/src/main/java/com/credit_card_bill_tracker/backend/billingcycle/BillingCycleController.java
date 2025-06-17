package com.credit_card_bill_tracker.backend.billingcycle;

import com.credit_card_bill_tracker.backend.user.User;
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

    @GetMapping
    public List<BillingCycleResponseDTO> getAll(@AuthenticationPrincipal User user) {
        return service.getAll(user);
    }

    @GetMapping("/{id}")
    public BillingCycleResponseDTO getById(@AuthenticationPrincipal User user,
                                           @PathVariable UUID id) {
        return service.getById(user, id);
    }

    @PostMapping
    public BillingCycleResponseDTO create(@AuthenticationPrincipal User user,
                                          @RequestBody BillingCycleDTO dto) {
        return service.create(user, dto);
    }

    @PutMapping("/{id}")
    public BillingCycleResponseDTO update(@AuthenticationPrincipal User user,
                                          @PathVariable UUID id,
                                          @RequestBody BillingCycleDTO dto) {
        return service.update(user, id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        service.delete(user, id);
    }
}