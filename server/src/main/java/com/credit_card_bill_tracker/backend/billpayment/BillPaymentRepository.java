package com.credit_card_bill_tracker.backend.billpayment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BillPaymentRepository extends JpaRepository<BillPayment, UUID> {

    List<BillPayment> findByUserIdAndCompletedFalse(UUID userId);

    List<BillPayment> findByUserId(UUID userId);
}
