package com.credit_card_bill_tracker.backend.creditcard;

import com.credit_card_bill_tracker.backend.common.BaseRepository;

import java.util.List;
import java.util.UUID;

public interface CreditCardRepository extends BaseRepository<CreditCard, UUID> {
    List<CreditCard> findByUserId(UUID userId);
}