package com.credit_card_bill_tracker.backend.billpayment;

import com.credit_card_bill_tracker.backend.common.BaseRepository;

import java.util.List;
import java.util.UUID;

public interface BillPaymentRepository extends BaseRepository<BillPayment, UUID> {

    List<BillPayment> findByUserIdAndCompletedFalse(UUID userId);

    List<BillPayment> findByUserId(UUID userId);
}
