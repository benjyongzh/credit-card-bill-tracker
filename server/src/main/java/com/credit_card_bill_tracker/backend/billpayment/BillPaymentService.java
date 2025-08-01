package com.credit_card_bill_tracker.backend.billpayment;

import com.credit_card_bill_tracker.backend.billpayment.BillPaymentRequestDTO;
import com.credit_card_bill_tracker.backend.common.errors.ResourceNotFoundException;
import com.credit_card_bill_tracker.backend.expensesummary.ExpenseSummaryService;
import com.credit_card_bill_tracker.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class BillPaymentService {

    private final BillPaymentRepository billPaymentRepository;
    private final BillPaymentMapper billPaymentMapper;
    private final ExpenseSummaryService summaryService;

    public List<BillPaymentResponseDTO> getAll(User user, UUID billingCycleId) {
        if (billingCycleId != null) {
            return billPaymentRepository.findByUserIdAndBillingCycleId(user.getId(), billingCycleId).stream()
                    .map(billPaymentMapper::toResponseDto)
                    .toList();
        }
        return billPaymentRepository.findByUserId(user.getId()).stream()
                .map(billPaymentMapper::toResponseDto)
                .toList();
    }

    public BillPaymentResponseDTO create(User user, BillPaymentRequestDTO dto) {
        BillPayment entity = billPaymentMapper.fromDto(dto);
        entity.setUser(user);

        BillPayment saved = billPaymentRepository.save(entity);
        summaryService.updateFromBillPayment(user, saved, true);

        return billPaymentMapper.toResponseDto(saved);
    }

    public BillPaymentResponseDTO update(User user, UUID id, BillPaymentRequestDTO dto) {
        BillPayment existing = billPaymentRepository.findById(id)
                .filter(p -> p.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Bill payment not found"));

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
        entity.softDelete();
        billPaymentRepository.save(entity);
    }

    @Transactional
    public List<BillPaymentResponseDTO> updateMany(User user, List<BillPaymentRequestDTO> dtos) {
        List<BillPaymentResponseDTO> result = new ArrayList<>();
        for (BillPaymentRequestDTO dto : dtos) {
            if (dto.getId() == null) continue;
            result.add(update(user, dto.getId(), dto));
        }
        return result;
    }

}
