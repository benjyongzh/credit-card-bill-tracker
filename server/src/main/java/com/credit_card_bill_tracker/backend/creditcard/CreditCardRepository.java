package com.credit_card_bill_tracker.backend.creditcard;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CreditCardRepository extends JpaRepository<CreditCard, UUID> {
    List<CreditCard> findByUserIdAndDeletedFalse(UUID userId);
}