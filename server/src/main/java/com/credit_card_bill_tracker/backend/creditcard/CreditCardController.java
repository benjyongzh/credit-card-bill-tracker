package com.credit_card_bill_tracker.backend.creditcard;

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
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CreditCardController {

    private final CreditCardService creditCardService;

    @Operation(summary = "Get credit cards", description = "Returns all credit cards belonging to the authenticated user")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CreditCardResponseDTO>>> getAll(@AuthenticationPrincipal User user) {
        List<CreditCardResponseDTO> result = creditCardService.getAll(user);
        return ApiResponseBuilder.ok(result);
    }

    @Operation(summary = "Create credit card", description = "Adds a new credit card for the user")
    @PostMapping
    public ResponseEntity<ApiResponse<CreditCardResponseDTO>> create(@AuthenticationPrincipal User user, @RequestBody CreditCardDTO dto) {
        CreditCardResponseDTO result = creditCardService.create(user, dto);
        return ApiResponseBuilder.created(result);
    }

    @Operation(summary = "Update credit card", description = "Updates an existing credit card identified by id")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CreditCardResponseDTO>> update(@AuthenticationPrincipal User user, @PathVariable UUID id, @RequestBody CreditCardDTO dto) {
        CreditCardResponseDTO result = creditCardService.update(user, id, dto);
        return ApiResponseBuilder.accepted(result);
    }

    @Operation(summary = "Delete credit card", description = "Removes the specified credit card")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCard(@AuthenticationPrincipal User user,
                                                        @PathVariable UUID id) {
        creditCardService.deleteCard(user, id);
        return ApiResponseBuilder.noContent();
    }
}
