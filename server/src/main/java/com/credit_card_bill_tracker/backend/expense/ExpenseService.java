package com.credit_card_bill_tracker.backend.expense;

import com.credit_card_bill_tracker.backend.bankaccount.BankAccount;
import com.credit_card_bill_tracker.backend.bankaccount.BankAccountRepository;
import com.credit_card_bill_tracker.backend.creditcard.CreditCard;
import com.credit_card_bill_tracker.backend.creditcard.CreditCardRepository;
import com.credit_card_bill_tracker.backend.spendingprofile.SpendingProfile;
import com.credit_card_bill_tracker.backend.spendingprofile.SpendingProfileRepository;
import com.credit_card_bill_tracker.backend.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository repository;
    private final ExpenseMapper mapper;
    private final CreditCardRepository creditCardRepo;
    private final BankAccountRepository bankAccountRepo;
    private final SpendingProfileRepository spendingProfileRepo;

    public List<ExpenseDTO> getAll(User user, UUID cardId) {
        if (cardId != null) {
            return repository.findByUserIdAndCreditCardIdAndDeletedFalse(user.getId(), cardId).stream()
                    .map(mapper::toDto)
                    .toList();
        } else {
            return repository.findByUserIdAndDeletedFalse(user.getId()).stream()
                    .map(mapper::toDto)
                    .toList();
        }
    }

    public ExpenseDTO create(User user, ExpenseDTO dto) {
        Expense entity = mapper.fromDto(dto);
        entity.setUser(user);
        entity.setCreditCard(creditCardRepo.findById(dto.getCreditCardId()).orElseThrow());
        Set<BankAccount> accounts = dto.getBankAccountIds().stream()
                .map(id -> bankAccountRepo.findById(id).orElseThrow())
                .collect(Collectors.toSet());
        entity.setBankAccounts(new ArrayList<>(accounts));
        return mapper.toDto(repository.save(entity));
    }

    public ExpenseDTO createWithSpendingProfile(User user, ExpenseCreateDTO dto, UUID spendingProfileId) {
        Expense entity = new Expense();
        entity.setUser(user);
        entity.setCreditCard(creditCardRepo.findById(dto.getCreditCardId()).orElseThrow());
        entity.setDate(dto.getDate());
        entity.setAmount(dto.getAmount());
        entity.setDescription(dto.getDescription());

        SpendingProfile profile = spendingProfileRepo.findById(spendingProfileId)
                .filter(p -> p.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Invalid spending profile"));

        entity.setBankAccounts(new ArrayList<>(profile.getBankAccounts()));

        return mapper.toDto(repository.save(entity));
    }

    public ExpenseDTO update(User user, UUID id, ExpenseDTO dto) {
        Expense entity = repository.findById(id)
                .filter(e -> e.getUser().getId().equals(user.getId()))
                .orElseThrow();

        mapper.updateEntityFromDto(entity, dto);
        entity.setCreditCard(creditCardRepo.findById(dto.getCreditCardId()).orElseThrow());
        Set<BankAccount> accounts = dto.getBankAccountIds().stream()
                .map(aid -> bankAccountRepo.findById(aid).orElseThrow())
                .collect(Collectors.toSet());
        entity.setBankAccounts(new ArrayList<>(accounts));

        return mapper.toDto(repository.save(entity));
    }

    public void delete(User user, UUID id) {
        Expense entity = repository.findById(id)
                .filter(e -> e.getUser().getId().equals(user.getId()))
                .orElseThrow();
        entity.setDeleted(true);
        repository.save(entity);
    }
}
