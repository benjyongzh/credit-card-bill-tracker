package com.credit_card_bill_tracker.backend.expense;

import com.credit_card_bill_tracker.backend.bankaccount.BankAccount;
import com.credit_card_bill_tracker.backend.expense.ExpenseRequestDTO;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.List;

@Component
public class ExpenseMapper {

    public ExpenseRequestDTO toDto(Expense entity) {
        ExpenseRequestDTO dto = new ExpenseRequestDTO();
        dto.setDate(entity.getDate());
        dto.setAmount(entity.getAmount());
        dto.setDescription(entity.getDescription());
        dto.setCreditCardId(entity.getCreditCard().getId());
        dto.setBankAccountIds(entity.getBankAccounts().stream()
                .map(BankAccount::getId)
                .collect(Collectors.toList()));
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
        return dto;
    }

    public Expense fromDto(ExpenseRequestDTO dto) {
        Expense entity = new Expense();
        entity.setDate(dto.getDate());
        entity.setAmount(dto.getAmount());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    public void updateEntityFromDto(Expense entity, ExpenseRequestDTO dto) {
        entity.setDate(dto.getDate());
        entity.setAmount(dto.getAmount());
        entity.setDescription(dto.getDescription());
    }
}
