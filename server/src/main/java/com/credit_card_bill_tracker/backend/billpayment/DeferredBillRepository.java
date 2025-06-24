package com.credit_card_bill_tracker.backend.billpayment;

import com.credit_card_bill_tracker.backend.common.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeferredBillRepository extends BaseRepository<DeferredBill, UUID> {
    List<DeferredBill> findByUserIdAndBillingCycleId(UUID userId, UUID billingCycleId);
}