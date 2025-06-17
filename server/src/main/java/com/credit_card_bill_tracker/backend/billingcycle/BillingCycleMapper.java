package com.credit_card_bill_tracker.backend.billingcycle;

import com.credit_card_bill_tracker.backend.billpayment.BillPayment;
import com.credit_card_bill_tracker.backend.billpayment.BillPaymentDTO;
import com.credit_card_bill_tracker.backend.billpayment.BillPaymentRepository;
import com.credit_card_bill_tracker.backend.billpayment.BillPaymentResponseDTO;
import com.credit_card_bill_tracker.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BillingCycleMapper {
    private final BillPaymentRepository billPaymentRepo;

    public BillingCycle toEntity(BillingCycleDTO dto, User user) {
        BillingCycle entity = new BillingCycle();
        entity.setUser(user);
        entity.setLabel(dto.getLabel());
        entity.setCompletedDate(dto.getCompletedDate());
        List<BillPayment> payments = dto.getBillPaymentIds().stream()
                .map(id -> billPaymentRepo.findById(id).orElseThrow())
                .toList();
        entity.setBillPayments(payments);
        return entity;
    }

    public void updateEntity(BillingCycle entity, BillingCycleDTO dto) {
        entity.setLabel(dto.getLabel());
        entity.setCompletedDate(dto.getCompletedDate());
        List<BillPayment> payments = dto.getBillPaymentIds().stream()
                .map(id -> billPaymentRepo.findById(id).orElseThrow())
                .toList();
        entity.setBillPayments(payments);
    }

    public BillingCycleResponseDTO toResponseDTO(BillingCycle entity) {
        BillingCycleResponseDTO dto = new BillingCycleResponseDTO();
        dto.setId(entity.getId());
        dto.setLabel(entity.getLabel());
        dto.setCompletedDate(entity.getCompletedDate());
        dto.setBillPayments(entity.getBillPayments().stream()
                .map(bp -> new BillPaymentResponseDTO(bp.getId(), bp.getFromAccount().getId(),
                        bp.getToCard() != null ? bp.getToCard().getId() : null,
                        bp.getToAccount() != null ? bp.getToAccount().getId() : null,
                        bp.getAmount(), bp.isCompleted()))
                .toList());
        return dto;
    }
}