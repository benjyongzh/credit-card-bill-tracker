package com.credit_card_bill_tracker.backend.creditcard;

import com.credit_card_bill_tracker.backend.user.User;
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

    @GetMapping
    public List<CreditCardResponseDTO> getAll(@AuthenticationPrincipal User user) {
        return creditCardService.getAll(user);
    }

    @PostMapping
    public CreditCardResponseDTO create(@AuthenticationPrincipal User user, @RequestBody CreditCardDTO dto) {
        return creditCardService.create(user, dto);
    }

    @PutMapping("/{id}")
    public CreditCardResponseDTO update(@AuthenticationPrincipal User user, @PathVariable UUID id, @RequestBody CreditCardDTO dto) {
        return creditCardService.update(user, id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteCard(@PathVariable UUID id) {
        creditCardService.deleteCard(id);
    }
}
