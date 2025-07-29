package com.credit_card_bill_tracker.backend.bankaccount;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.credit_card_bill_tracker.backend.user.User;
import com.credit_card_bill_tracker.backend.bankaccount.BankAccountRequestDTO;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Get all BankAccounts of a User", description = "Returns a list of BankAccounts")
    @GetMapping
    public ResponseEntity<List<BankAccountResponseDTO>> getAll(@AuthenticationPrincipal User user) {
        List<BankAccountResponseDTO> result = service.getAll(user);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Create BankAccount", description = "Adds a new bank account for the authenticated user")
    @PostMapping
    public ResponseEntity<BankAccountResponseDTO> create(@AuthenticationPrincipal User user, @Valid @RequestBody BankAccountRequestDTO dto) {
        BankAccountResponseDTO result = service.create(user, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Update BankAccount", description = "Modifies an existing bank account identified by id")
    @PutMapping("/{id}")
    public ResponseEntity<BankAccountResponseDTO> update(@AuthenticationPrincipal User user, @PathVariable UUID id, @Valid @RequestBody BankAccountRequestDTO dto) {
        BankAccountResponseDTO result = service.update(user, id, dto);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(result);
    }

    @Operation(summary = "Delete BankAccount", description = "Soft deletes the bank account with the given id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        service.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}
