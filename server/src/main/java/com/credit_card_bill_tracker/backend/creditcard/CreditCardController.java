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
    public List<CreditCard> getCards(@AuthenticationPrincipal User user) {
        return creditCardService.getUserCards(user.getId());
    }

    @PostMapping
    public CreditCard createCard(@AuthenticationPrincipal User user, @RequestBody CreditCardDTO dto) {
        return creditCardService.createCard(user, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteCard(@PathVariable UUID id) {
        creditCardService.deleteCard(id);
    }
}
