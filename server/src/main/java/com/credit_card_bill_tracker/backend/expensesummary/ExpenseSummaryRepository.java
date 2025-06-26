package com.credit_card_bill_tracker.backend.expensesummary;

import com.credit_card_bill_tracker.backend.common.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExpenseSummaryRepository extends BaseRepository<ExpenseSummary, UUID> {
    List<ExpenseSummary> findByUserId(UUID userId);
    Optional<ExpenseSummary> findByUserIdAndFromAccountIdAndToIdAndToType(UUID userId, UUID fromAccountId, UUID toId, String toType);
}