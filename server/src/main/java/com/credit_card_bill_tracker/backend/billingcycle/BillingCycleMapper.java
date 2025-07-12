package com.credit_card_bill_tracker.backend.billingcycle;

import com.credit_card_bill_tracker.backend.billpayment.BillPayment;
import com.credit_card_bill_tracker.backend.billpayment.BillPaymentMapper;
import com.credit_card_bill_tracker.backend.billpayment.BillPaymentRepository;
import com.credit_card_bill_tracker.backend.common.errors.ResourceNotFoundException;
import com.credit_card_bill_tracker.backend.billingcycle.BillingCycleRequestDTO;
import com.credit_card_bill_tracker.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BillingCycleMapper {
    private final BillPaymentRepository billPaymentRepo;
    private final BillPaymentMapper billPaymentMapper;

    public BillingCycle toEntity(BillingCycleRequestDTO dto, User user) {
        BillingCycle entity = new BillingCycle();
        entity.setUser(user);
        entity.setLabel(dto.getLabel());
        entity.setCompletedDate(dto.getCompletedDate());
        List<BillPayment> payments = dto.getBillPaymentIds().stream()
                .map(id -> billPaymentRepo.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Bill payment not found")))
                .toList();
        entity.setBillPayments(payments);
        return entity;
    }

    public void updateEntity(BillingCycle entity, BillingCycleRequestDTO dto) {
        entity.setLabel(dto.getLabel());
        entity.setCompletedDate(dto.getCompletedDate());
        List<BillPayment> payments = dto.getBillPaymentIds().stream()
                .map(id -> billPaymentRepo.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Bill payment not found")))
                .toList();
        entity.setBillPayments(payments);
    }

    public BillingCycleResponseDTO toResponseDTO(BillingCycle entity) {
        BillingCycleResponseDTO dto = new BillingCycleResponseDTO();
        dto.setId(entity.getId());
        dto.setLabel(entity.getLabel());
        dto.setCompletedDate(entity.getCompletedDate());
        dto.setBillPayments(entity.getBillPayments().stream()
                .map(billPaymentMapper::toResponseDto)
                .toList());
        return dto;
    }
}
