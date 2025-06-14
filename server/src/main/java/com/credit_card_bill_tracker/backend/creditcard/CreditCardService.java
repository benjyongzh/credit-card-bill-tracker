package com.credit_card_bill_tracker.backend.creditcard;

import com.credit_card_bill_tracker.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreditCardService {

    private final CreditCardRepository creditCardRepo;

    public List<CreditCard> getUserCards(UUID userId) {
        return creditCardRepo.findByUserIdAndDeletedFalse(userId);
    }

    public CreditCard createCard(User user, CreditCardDTO dto) {
        CreditCard card = new CreditCard();
        card.setUser(user);
        card.setCardName(dto.getCardName());
        card.setLastFourDigits(dto.getLastFourDigits());
        return creditCardRepo.save(card);
    }

    public void deleteCard(UUID id) {
        CreditCard card = creditCardRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        card.setDeleted(true);
        creditCardRepo.save(card);
    }
}
