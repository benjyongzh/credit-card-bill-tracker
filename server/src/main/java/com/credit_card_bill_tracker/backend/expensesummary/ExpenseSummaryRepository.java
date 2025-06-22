package com.credit_card_bill_tracker.backend.expensesummary;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExpenseSummaryRepository extends org.springframework.data.jpa.repository.JpaRepository<ExpenseSummary, UUID> {
    List<ExpenseSummary> findByUserId(UUID userId);
    Optional<ExpenseSummary> findByUserIdAndFromAccountIdAndToCardId(UUID userId, UUID fromAccountId, UUID toCardId);
    Optional<ExpenseSummary> findByUserIdAndFromAccountIdAndToAccountId(UUID userId, UUID fromAccountId, UUID toAccountId);
}