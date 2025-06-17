package com.credit_card_bill_tracker.backend.billingcycle;

import com.credit_card_bill_tracker.backend.billpayment.BillPayment;
import com.credit_card_bill_tracker.backend.billpayment.BillPaymentRepository;
import com.credit_card_bill_tracker.backend.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BillingCycleService {

    private final BillingCycleRepository repository;
    private final BillingCycleMapper mapper;
    private final BillPaymentRepository billPaymentRepo;

    public List<BillingCycleResponseDTO> getAll(User user) {
        return repository.findByUserIdAndDeletedFalse(user.getId()).stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    public BillingCycleResponseDTO getById(User user, UUID id) {
        BillingCycle cycle = repository.findById(id)
                .filter(c -> c.getUser().getId().equals(user.getId()))
                .orElseThrow();
        return mapper.toResponseDTO(cycle);
    }

    @Transactional
    public BillingCycleResponseDTO create(User user, BillingCycleDTO dto) {
        BillingCycle cycle = new BillingCycle();
        cycle.setUser(user);
        cycle.setLabel(dto.getLabel());
        cycle.setCompletedDate(dto.getCompletedDate());
        List<BillPayment> payments = dto.getBillPaymentIds().stream()
                .map(id -> billPaymentRepo.findById(id).orElseThrow())
                .toList();
        cycle.setBillPayments(payments);
        return mapper.toResponseDTO(repository.save(cycle));
    }

    @Transactional
    public BillingCycleResponseDTO update(User user, UUID id, BillingCycleDTO dto) {
        BillingCycle cycle = repository.findById(id)
                .filter(c -> c.getUser().getId().equals(user.getId()))
                .orElseThrow();
        mapper.updateEntity(cycle, dto);
        List<BillPayment> payments = dto.getBillPaymentIds().stream()
                .map(billPaymentRepo::findById)
                .map(Optional::orElseThrow)
                .toList();
        cycle.setBillPayments(payments);
        return mapper.toResponseDTO(repository.save(cycle));
    }

    @Transactional
    public void delete(User user, UUID id) {
        BillingCycle cycle = repository.findById(id)
                .filter(c -> c.getUser().getId().equals(user.getId()))
                .orElseThrow();
        cycle.setDeleted(true);
        repository.save(cycle);
    }
}