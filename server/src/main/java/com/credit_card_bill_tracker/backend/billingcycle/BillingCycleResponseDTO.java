package com.credit_card_bill_tracker.backend.billingcycle;

import com.credit_card_bill_tracker.backend.billpayment.BillPaymentDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillingCycleResponseDTO {
    private UUID id;
    private String label;
    private LocalDate completedDate;
    private List<BillPaymentDTO> billPayments;
}