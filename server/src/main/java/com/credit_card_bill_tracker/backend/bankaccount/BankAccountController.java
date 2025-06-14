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

    private final BankAccountService bankAccountService;

    @GetMapping
    public List<BankAccount> getAccounts(@AuthenticationPrincipal User user) {
        return bankAccountService.getUserAccounts(user.getId());
    }

    @PostMapping
    public BankAccount createAccount(@AuthenticationPrincipal User user, @RequestBody BankAccountDTO dto) {
        return bankAccountService.createAccount(user, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteAccount(@PathVariable UUID id) {
        bankAccountService.deleteAccount(id);
    }
}
