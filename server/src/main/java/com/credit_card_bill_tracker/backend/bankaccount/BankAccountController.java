package com.credit_card_bill_tracker.backend.bankaccount;

import com.credit_card_bill_tracker.backend.common.ApiResponse;
import com.credit_card_bill_tracker.backend.common.ApiResponseBuilder;
import com.credit_card_bill_tracker.backend.user.User;
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

    @GetMapping
    public ResponseEntity<ApiResponse<List<BankAccountResponseDTO>>> getAll(@AuthenticationPrincipal User user) {
        List<BankAccountResponseDTO> result = service.getAll(user);
        return ApiResponseBuilder.ok(result);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BankAccountResponseDTO>> create(@AuthenticationPrincipal User user, @RequestBody BankAccountDTO dto) {
        BankAccountResponseDTO result = service.create(user, dto);
        return ApiResponseBuilder.created(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BankAccountResponseDTO>> update(@AuthenticationPrincipal User user, @PathVariable UUID id, @RequestBody BankAccountDTO dto) {
        BankAccountResponseDTO result = service.update(user, id, dto);
        return ApiResponseBuilder.accepted(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        service.delete(user, id);
        return ApiResponseBuilder.noContent();
    }
}
