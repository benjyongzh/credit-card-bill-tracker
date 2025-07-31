package com.credit_card_bill_tracker.backend.billingcycle;

import com.credit_card_bill_tracker.backend.bankaccount.BankAccount;
import com.credit_card_bill_tracker.backend.billpayment.BillOptimizerService;
import com.credit_card_bill_tracker.backend.billpayment.BillSuggestionDTO;
import com.credit_card_bill_tracker.backend.billpayment.DeferredBill;
import com.credit_card_bill_tracker.backend.billpayment.DeferredBillMapper;
import com.credit_card_bill_tracker.backend.billpayment.DeferredBillRepository;
import com.credit_card_bill_tracker.backend.billpayment.OptimizationStrategy;
import com.credit_card_bill_tracker.backend.expensesummary.TargetType;
import com.credit_card_bill_tracker.backend.common.errors.ResourceNotFoundException;
import com.credit_card_bill_tracker.backend.common.errors.BadRequestException;
import com.credit_card_bill_tracker.backend.creditcard.CreditCard;
import com.credit_card_bill_tracker.backend.user.User;
import com.credit_card_bill_tracker.backend.billingcycle.BillingCycleRequestDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BillingCycleService {

    private final BillingCycleRepository repository;
    private final BillingCycleMapper billingCycleMapper;
    private final DeferredBillRepository deferredBillRepo;
    private final DeferredBillMapper deferredBillMapper;
    private final BillOptimizerService billOptimizerService;

    public List<BillingCycleResponseDTO> getAll(User user) {
        return repository.findByUserId(user.getId()).stream()
                .map(billingCycleMapper::toResponseDTO)
                .toList();
    }

    public BillingCycleResponseDTO getLatest(User user) {
        BillingCycle latest = repository.findFirstByUserIdOrderByUpdatedAtDesc(user.getId());
        if (latest == null) return null;
        return billingCycleMapper.toResponseDTO(latest);
    }

    public BillingCycleResponseDTO getById(User user, UUID id) {
        BillingCycle cycle = repository.findById(id)
                .filter(c -> c.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Billing cycle not found"));
        return billingCycleMapper.toResponseDTO(cycle);
    }

    @Transactional
    public BillingCycleResponseDTO create(User user, BillingCycleRequestDTO dto) {
        if (repository.existsByUserIdAndLabel(user.getId(), dto.getLabel()) ||
                repository.existsByUserIdAndMonth(user.getId(), dto.getMonth())) {
            throw new BadRequestException("Billing cycle with that month or label already exists");
        }
        BillingCycle entity = billingCycleMapper.toEntity(dto, user);
        BillingCycle savedCycle = repository.save(entity);

        List<BillSuggestionDTO> suggestions = billOptimizerService.computeBillSuggestions(user, OptimizationStrategy.MINIMIZE_TRANSACTIONS);
        List<DeferredBill> deferreds = new ArrayList<>();

        for (BillSuggestionDTO suggestion : suggestions) {

            DeferredBill db = new DeferredBill();
            db.setUser(user);
            BankAccount from = new BankAccount();
            from.setId(suggestion.getFrom());
            db.setFromAccount(from);

            db.setAmount(suggestion.getAmount());
            db.setBillingCycle(savedCycle);

            if (suggestion.getToType() == TargetType.CARD) {
                CreditCard toCard = new CreditCard();
                toCard.setId(suggestion.getTo());
                db.setToCard(toCard);
            } else {
                BankAccount toAccount = new BankAccount();
                toAccount.setId(suggestion.getTo());
                db.setToAccount(toAccount);
            }
            deferreds.add(db);
        }

        deferredBillRepo.saveAll(deferreds);
        return billingCycleMapper.toResponseDTO(savedCycle);
    }

    @Transactional
    public BillingCycleResponseDTO update(User user, UUID id, BillingCycleRequestDTO dto) {
        BillingCycle cycle = repository.findById(id)
                .filter(c -> c.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Billing cycle not found"));
        if (repository.existsByUserIdAndLabelAndIdNot(user.getId(), dto.getLabel(), id) ||
                repository.existsByUserIdAndMonthAndIdNot(user.getId(), dto.getMonth(), id)) {
            throw new BadRequestException("Billing cycle with that month or label already exists");
        }
        billingCycleMapper.updateEntity(cycle, dto);
        return billingCycleMapper.toResponseDTO(repository.save(cycle));
    }

    @Transactional
    public BillingCycleResponseDTO complete(User user, UUID id) {
        BillingCycle cycle = repository.findById(id)
                .filter(c -> c.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Billing cycle not found"));
        if (cycle.getCompletedDate() == null) {
            cycle.setCompletedDate(LocalDate.now());
            repository.save(cycle);
        }
        return billingCycleMapper.toResponseDTO(cycle);
    }

    @Transactional
    public void delete(User user, UUID id) {
        BillingCycle cycle = repository.findById(id)
                .filter(c -> c.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Billing cycle not found"));
        cycle.softDelete();
        repository.save(cycle);
    }

    public String setNewBillingCycleDefaultLabel(LocalDate date) {
        return date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + date.getYear();
    }

    public List<DeferredBillResponseDTO> getDeferredBills(User user, UUID billingCycleId) {
        return deferredBillRepo.findByUserIdAndBillingCycleId(user.getId(), billingCycleId).stream()
                .map(deferredBillMapper::toResponseDto)
                .toList();
    }
}
