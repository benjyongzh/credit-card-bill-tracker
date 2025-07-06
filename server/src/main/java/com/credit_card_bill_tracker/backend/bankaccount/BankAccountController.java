package com.credit_card_bill_tracker.backend.bankaccount;

import com.credit_card_bill_tracker.backend.common.ApiResponse;
import com.credit_card_bill_tracker.backend.common.ApiResponseBuilder;
import com.credit_card_bill_tracker.backend.user.User;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<List<BankAccountResponseDTO>>> getAll(@AuthenticationPrincipal User user) {
        List<BankAccountResponseDTO> result = service.getAll(user);
        return ApiResponseBuilder.ok(result);
    }

    @Operation(summary = "Create BankAccount", description = "Adds a new bank account for the authenticated user")
    @PostMapping
    public ResponseEntity<ApiResponse<BankAccountResponseDTO>> create(@AuthenticationPrincipal User user, @RequestBody BankAccountDTO dto) {
        BankAccountResponseDTO result = service.create(user, dto);
        return ApiResponseBuilder.created(result);
    }

    @Operation(summary = "Update BankAccount", description = "Modifies an existing bank account identified by id")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BankAccountResponseDTO>> update(@AuthenticationPrincipal User user, @PathVariable UUID id, @RequestBody BankAccountDTO dto) {
        BankAccountResponseDTO result = service.update(user, id, dto);
        return ApiResponseBuilder.accepted(result);
    }

    @Operation(summary = "Delete BankAccount", description = "Soft deletes the bank account with the given id")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        service.delete(user, id);
        return ApiResponseBuilder.noContent();
    }
}
