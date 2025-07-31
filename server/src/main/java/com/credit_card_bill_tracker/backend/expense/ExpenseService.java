package com.credit_card_bill_tracker.backend.expense;

import com.credit_card_bill_tracker.backend.bankaccount.BankAccount;
import com.credit_card_bill_tracker.backend.bankaccount.BankAccountRepository;
import com.credit_card_bill_tracker.backend.common.errors.BadRequestException;
import com.credit_card_bill_tracker.backend.common.errors.ResourceNotFoundException;
import com.credit_card_bill_tracker.backend.creditcard.CreditCardRepository;
import com.credit_card_bill_tracker.backend.expensesummary.ExpenseSummaryService;
import com.credit_card_bill_tracker.backend.spendingprofile.SpendingProfile;
import com.credit_card_bill_tracker.backend.spendingprofile.SpendingProfileRepository;
import com.credit_card_bill_tracker.backend.user.User;
import com.credit_card_bill_tracker.backend.expense.ExpenseRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository repository;
    private final ExpenseMapper mapper;
    private final CreditCardRepository creditCardRepo;
    private final BankAccountRepository bankAccountRepo;
    private final SpendingProfileRepository spendingProfileRepo;
    private final ExpenseSummaryService summaryService;

    public List<ExpenseResponseDTO> getAll(User user, UUID cardId) {
        if (cardId != null) {
            return repository.findByUserIdAndCreditCardId(user.getId(), cardId).stream()
                    .map(mapper::toResponseDto)
                    .toList();
        } else {
            return repository.findByUserId(user.getId()).stream()
                    .map(mapper::toResponseDto)
                    .toList();
        }
    }

//    public ExpenseDTO create(User user, ExpenseDTO dto) {
//        Expense entity = mapper.fromDto(dto);
//        entity.setUser(user);
//        entity.setCreditCard(creditCardRepo.findById(dto.getCreditCardId())
//                .orElseThrow(() -> new ResourceNotFoundException("Credit card not found")));
//        Set<BankAccount> accounts = dto.getBankAccountIds().stream()
//                .map(id -> bankAccountRepo.findById(id)
//                        .orElseThrow(() -> new ResourceNotFoundException("Bank account not found")))
//                .collect(Collectors.toSet());
//        entity.setBankAccounts(new ArrayList<>(accounts));
//        return mapper.toDto(repository.save(entity));
//    }

    public ExpenseResponseDTO createWithSpendingProfile(User user, ExpenseRequestDTO dto, UUID spendingProfileId) {
        Expense entity = mapper.fromDto(dto);
        entity.setUser(user);
        entity.setCreditCard(creditCardRepo.findById(dto.getCreditCardId())
                .orElseThrow(() -> new ResourceNotFoundException("Credit card not found")));

        SpendingProfile profile = spendingProfileRepo.findById(spendingProfileId)
                .filter(p -> p.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new BadRequestException("Invalid spending profile"));
        entity.setBankAccounts(new ArrayList<>(profile.getBankAccounts()));
        Expense saved = repository.save(entity);
        summaryService.updateFromExpense(user, saved, true);

        return mapper.toResponseDto(saved);
    }

    public ExpenseResponseDTO update(User user, UUID id, ExpenseRequestDTO dto) {
        Expense entity = repository.findById(id)
                .filter(e -> e.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        summaryService.updateFromExpense(user, entity, false); // reverse old

        mapper.updateEntityFromDto(entity, dto);
        entity.setCreditCard(creditCardRepo.findById(dto.getCreditCardId())
                .orElseThrow(() -> new ResourceNotFoundException("Credit card not found")));
        List<BankAccount> accounts = dto.getBankAccountIds().stream()
                .map(aid -> bankAccountRepo.findById(aid)
                        .orElseThrow(() -> new ResourceNotFoundException("Bank account not found")))
                .toList();
        entity.setBankAccounts(new ArrayList<>(accounts));

        Expense saved = repository.save(entity);
        summaryService.updateFromExpense(user, saved, true); // apply new

        return mapper.toResponseDto(saved);
    }

    public void delete(User user, UUID id) {
        Expense entity = repository.findById(id)
                .filter(e -> e.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));
        summaryService.updateFromExpense(user, entity, false);
        entity.softDelete();
        repository.save(entity);
    }
}
