package com.credit_card_bill_tracker.backend.billingcycle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BillingCycleRepository extends JpaRepository<BillingCycle, UUID> {
    List<BillingCycle> findByUserIdAndDeletedFalse(UUID userId);
}