package com.credit_card_bill_tracker.backend.billpayment;

import com.credit_card_bill_tracker.backend.billingcycle.*;
import com.credit_card_bill_tracker.backend.common.BaseEntity;
import com.credit_card_bill_tracker.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BillPaymentService {

    private final BillPaymentRepository billPaymentRepository;
    private final BillPaymentMapper billPaymentMapper;
    private final BillingCycleService cycleService;

    public List<BillPaymentResponseDTO> getAll(User user) {
        return billPaymentRepository.findByUserId(user.getId()).stream()
                .map(billPaymentMapper::toResponseDto)
                .toList();
    }

    public BillPaymentResponseDTO create(User user, BillPaymentDTO dto) {
        BillPayment entity = billPaymentMapper.fromDto(dto);
        entity.setUser(user);
        entity.setCompleted(false);
        return billPaymentMapper.toResponseDto(billPaymentRepository.save(entity));
    }

    public BillPaymentResponseDTO update(User user, UUID id, BillPaymentDTO dto) {
        BillPayment entity = billPaymentRepository.findById(id)
                .filter(bp -> bp.getUser().getId().equals(user.getId()))
                .orElseThrow();

        if (entity.isCompleted()) {
            throw new IllegalStateException("Cannot edit a completed bill payment.");
        }

        billPaymentMapper.updateEntityFromDto(entity, dto);
        return billPaymentMapper.toResponseDto(billPaymentRepository.save(entity));
    }

    public void delete(User user, UUID id) {
        BillPayment entity = billPaymentRepository.findById(id)
                .filter(bp -> bp.getUser().getId().equals(user.getId()))
                .orElseThrow();

        if (entity.isCompleted()) {
            throw new IllegalStateException("Cannot delete a completed bill payment.");
        }

        billPaymentRepository.delete(entity);
    }

    public BillingCycleResponseDTO markBillsComplete(User user) {
        List<BillPayment> inProgress = billPaymentRepository.findByUserIdAndCompletedFalse(user.getId());
        if (inProgress.isEmpty()) throw new IllegalStateException("No bill payments to complete.");
        for (BillPayment bp : inProgress) {
            bp.setCompleted(true);
        }
        billPaymentRepository.saveAll(inProgress);
        BillingCycleDTO cycle = new BillingCycleDTO();
        cycle.setBillPaymentIds(inProgress.stream().map(BaseEntity::getId).toList());
        LocalDate now = LocalDate.now();
        cycle.setCompletedDate(now);
        cycle.setLabel(cycleService.setNewBillingCycleDefaultLabel(now));
        return cycleService.create(user, cycle);
    }
}

