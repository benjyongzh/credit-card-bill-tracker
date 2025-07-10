package com.credit_card_bill_tracker.backend.expensesummary;

import com.credit_card_bill_tracker.backend.common.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExpenseSummaryRepository extends BaseRepository<ExpenseSummary, UUID> {
    List<ExpenseSummary> findByUserId(UUID userId);
    Optional<ExpenseSummary> findByUserIdAndFromAccountIdAndToIdAndToType(UUID userId, UUID fromAccountId, UUID toId, TargetType toType);
    @Query(value = """
    SELECT * FROM expense_summaries
    WHERE user_id = :userId
      AND from_account_id = :fromAccountId
      AND to_id = :toId
      AND to_type = :toType
      AND deleted_at IS NULL
    """, nativeQuery = true)
    Optional<ExpenseSummary> findExactNative(
            @Param("userId") UUID userId,
            @Param("fromAccountId") UUID fromAccountId,
            @Param("toId") UUID toId,
            @Param("toType") String toType
    );
}