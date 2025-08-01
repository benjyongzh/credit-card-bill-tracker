package com.credit_card_bill_tracker.backend.expense;

import com.credit_card_bill_tracker.backend.bankaccount.BankAccount;
import com.credit_card_bill_tracker.backend.billingcycle.BillingCycleRepository;
import com.credit_card_bill_tracker.backend.expense.ExpenseRequestDTO;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.List;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExpenseMapper {

    private final BillingCycleRepository billingCycleRepository;

    public ExpenseRequestDTO toDto(Expense entity) {
        ExpenseRequestDTO dto = new ExpenseRequestDTO();
        dto.setId(entity.getId());
        dto.setDate(entity.getDate());
        dto.setAmount(entity.getAmount());
        dto.setDescription(entity.getDescription());
        dto.setCreditCardId(entity.getCreditCard().getId());
        dto.setBankAccountIds(entity.getBankAccounts().stream()
                .map(BankAccount::getId)
                .collect(Collectors.toList()));
        dto.setBillingCycleId(entity.getBillingCycle().getId());
        return dto;
    }

    public ExpenseResponseDTO toResponseDto(Expense entity) {
        ExpenseResponseDTO dto = new ExpenseResponseDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUser().getId());
        dto.setDate(entity.getDate());
        dto.setAmount(entity.getAmount());
        dto.setDescription(entity.getDescription());
        dto.setCreditCardId(entity.getCreditCard().getId());
        dto.setBankAccountIds(entity.getBankAccounts().stream()
                .map(BankAccount::getId)
                .collect(Collectors.toList()));
        dto.setBillingCycleId(entity.getBillingCycle().getId());
        return dto;
    }

    public Expense fromDto(ExpenseRequestDTO dto) {
        Expense entity = new Expense();
        entity.setDate(dto.getDate());
        entity.setAmount(dto.getAmount());
        entity.setDescription(dto.getDescription());
        entity.setBillingCycle(billingCycleRepository.getReferenceById(dto.getBillingCycleId()));
        return entity;
    }

    public void updateEntityFromDto(Expense entity, ExpenseRequestDTO dto) {
        entity.setDate(dto.getDate());
        entity.setAmount(dto.getAmount());
        entity.setDescription(dto.getDescription());
        entity.setBillingCycle(billingCycleRepository.getReferenceById(dto.getBillingCycleId()));
    }
}
