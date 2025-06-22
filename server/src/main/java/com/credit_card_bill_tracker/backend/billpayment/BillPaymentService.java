package com.credit_card_bill_tracker.backend.billpayment;

import com.credit_card_bill_tracker.backend.billingcycle.*;
import com.credit_card_bill_tracker.backend.common.BaseEntity;
import com.credit_card_bill_tracker.backend.common.errors.BadRequestException;
import com.credit_card_bill_tracker.backend.common.errors.ResourceNotFoundException;
import com.credit_card_bill_tracker.backend.expensesummary.ExpenseSummaryService;
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
    private final ExpenseSummaryService summaryService;

    public List<BillPaymentResponseDTO> getAll(User user) {
        return billPaymentRepository.findByUserId(user.getId()).stream()
                .map(billPaymentMapper::toResponseDto)
                .toList();
    }

    public BillPaymentResponseDTO create(User user, BillPaymentDTO dto) {
        BillPayment entity = billPaymentMapper.fromDto(dto);
        entity.setUser(user);
        entity.setCompleted(false);

        BillPayment saved = billPaymentRepository.save(entity);
        summaryService.updateFromBillPayment(user, saved, true);

        return billPaymentMapper.toResponseDto(saved);
    }

    public BillPaymentResponseDTO update(User user, UUID id, BillPaymentDTO dto) {
        BillPayment existing = billPaymentRepository.findById(id)
                .filter(p -> p.getUser().getId().equals(user.getId()))
                .orElseThrow();

        summaryService.updateFromBillPayment(user, existing, false); // reverse old

        billPaymentMapper.updateEntityFromDto(existing, dto);
        BillPayment saved = billPaymentRepository.save(existing);

        summaryService.updateFromBillPayment(user, saved, true); // apply new

        return billPaymentMapper.toResponseDto(saved);
    }

    public void delete(User user, UUID id) {
        BillPayment entity = billPaymentRepository.findById(id)
                .filter(bp -> bp.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Bill payment not found"));

//        if (entity.isCompleted()) {
//            throw new BadRequestException("Cannot delete a completed bill payment.");
//        }
        summaryService.updateFromBillPayment(user, entity, false);
        entity.setDeleted(true);
        billPaymentRepository.save(entity);
    }

    public BillingCycleResponseDTO markBillsComplete(User user) {
        List<BillPayment> inProgress = billPaymentRepository.findByUserIdAndCompletedFalse(user.getId());
        if (inProgress.isEmpty()) throw new BadRequestException("No bill payments to complete.");
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
