package com.credit_card_bill_tracker.backend.creditcard;

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
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CreditCardController {

    private final CreditCardService creditCardService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CreditCardResponseDTO>>> getAll(@AuthenticationPrincipal User user) {
        List<CreditCardResponseDTO> result = creditCardService.getAll(user);
        return ApiResponseBuilder.ok(result);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CreditCardResponseDTO>> create(@AuthenticationPrincipal User user, @RequestBody CreditCardDTO dto) {
        CreditCardResponseDTO result = creditCardService.create(user, dto);
        return ApiResponseBuilder.created(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CreditCardResponseDTO>> update(@AuthenticationPrincipal User user, @PathVariable UUID id, @RequestBody CreditCardDTO dto) {
        CreditCardResponseDTO result = creditCardService.update(user, id, dto);
        return ApiResponseBuilder.accepted(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCard(@PathVariable UUID id) {
        creditCardService.deleteCard(id);
        return ApiResponseBuilder.noContent();
    }
}
