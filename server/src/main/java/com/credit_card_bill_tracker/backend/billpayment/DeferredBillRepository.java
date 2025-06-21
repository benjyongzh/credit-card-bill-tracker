package com.credit_card_bill_tracker.backend.billpayment;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeferredBillRepository extends org.springframework.data.jpa.repository.JpaRepository<DeferredBill, UUID> {
    List<DeferredBill> findByUserIdAndBillingCycleId(UUID userId, UUID billingCycleId);
}