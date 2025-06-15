package com.credit_card_bill_tracker.backend.bankaccount;

import com.credit_card_bill_tracker.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountService service;

    @GetMapping
    public List<BankAccountDTO> getAll(@AuthenticationPrincipal User user) {
        return service.getAll(user);
    }

    @PostMapping
    public BankAccountDTO create(@AuthenticationPrincipal User user, @RequestBody BankAccountDTO dto) {
        return service.create(user, dto);
    }

    @PutMapping("/{id}")
    public BankAccountDTO update(@AuthenticationPrincipal User user, @PathVariable UUID id, @RequestBody BankAccountDTO dto) {
        return service.update(user, id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        service.delete(user, id);
    }
}
