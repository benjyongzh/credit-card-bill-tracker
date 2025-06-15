package com.credit_card_bill_tracker.backend.billpayment;

import com.credit_card_bill_tracker.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BillPaymentService {

    private final BillPaymentRepository billPaymentRepository;
    private final BillPaymentMapper billPaymentMapper;

    public List<BillPaymentDTO> getAll(User user) {
        return billPaymentRepository.findByUserId(user.getId()).stream()
                .map(billPaymentMapper::toDto)
                .toList();
    }

    public BillPaymentDTO create(User user, BillPaymentDTO dto) {
        BillPayment entity = billPaymentMapper.fromDto(dto);
        entity.setUser(user);
        entity.setCompleted(false);
        return billPaymentMapper.toDto(billPaymentRepository.save(entity));
    }

    public BillPaymentDTO update(User user, UUID id, BillPaymentDTO dto) {
        BillPayment entity = billPaymentRepository.findById(id)
                .filter(bp -> bp.getUser().getId().equals(user.getId()))
                .orElseThrow();

        if (entity.isCompleted()) {
            throw new IllegalStateException("Cannot edit a completed bill payment.");
        }

        billPaymentMapper.updateEntityFromDto(entity, dto);
        return billPaymentMapper.toDto(billPaymentRepository.save(entity));
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
}

