package com.credit_card_bill_tracker.backend.expense;

import com.credit_card_bill_tracker.backend.common.BaseRepository;

import java.util.List;
import java.util.UUID;

public interface ExpenseRepository extends BaseRepository<Expense, UUID> {
    List<Expense> findByUserId(UUID userId);
    List<Expense> findByUserIdAndCreditCardId(UUID userId, UUID cardId);
    List<Expense> findByUserIdAndBillingCycleId(UUID userId, UUID billingCycleId);
}