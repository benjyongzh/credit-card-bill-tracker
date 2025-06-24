package com.credit_card_bill_tracker.backend.billingcycle;

import com.credit_card_bill_tracker.backend.common.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BillingCycleRepository extends BaseRepository<BillingCycle, UUID> {
    List<BillingCycle> findByUserId(UUID userId);
}