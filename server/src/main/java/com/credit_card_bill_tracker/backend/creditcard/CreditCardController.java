package com.credit_card_bill_tracker.backend.creditcard;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.credit_card_bill_tracker.backend.user.User;
import com.credit_card_bill_tracker.backend.creditcard.CreditCardRequestDTO;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<CreditCardResponseDTO>> getAll(@AuthenticationPrincipal User user) {
        List<CreditCardResponseDTO> result = creditCardService.getAll(user);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Create credit card", description = "Adds a new credit card for the user")
    @PostMapping
    public ResponseEntity<CreditCardResponseDTO> create(@AuthenticationPrincipal User user, @Valid @RequestBody CreditCardRequestDTO dto) {
        CreditCardResponseDTO result = creditCardService.create(user, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Update credit card", description = "Updates an existing credit card identified by id")
    @PutMapping("/{id}")
    public ResponseEntity<CreditCardResponseDTO> update(@AuthenticationPrincipal User user, @PathVariable UUID id, @Valid @RequestBody CreditCardRequestDTO dto) {
        CreditCardResponseDTO result = creditCardService.update(user, id, dto);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(result);
    }

    @Operation(summary = "Delete credit card", description = "Removes the specified credit card")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@AuthenticationPrincipal User user,
                                           @PathVariable UUID id) {
        creditCardService.deleteCard(user, id);
        return ResponseEntity.noContent().build();
    }
}
