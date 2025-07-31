package com.credit_card_bill_tracker.backend.billingcycle;

import com.credit_card_bill_tracker.backend.common.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BillingCycleRepository extends BaseRepository<BillingCycle, UUID> {
    List<BillingCycle> findByUserId(UUID userId);

    BillingCycle findFirstByUserIdOrderByUpdatedAtDesc(UUID userId);

    boolean existsByUserIdAndLabel(UUID userId, String label);

    boolean existsByUserIdAndMonth(UUID userId, java.time.Month month);

    boolean existsByUserIdAndLabelAndIdNot(UUID userId, String label, UUID id);

    boolean existsByUserIdAndMonthAndIdNot(UUID userId, java.time.Month month, UUID id);
}