package com.credit_card_bill_tracker.backend.expense;

import com.credit_card_bill_tracker.backend.bankaccount.BankAccount;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ExpenseMapper {

    public ExpenseDTO toDto(Expense entity) {
        ExpenseDTO dto = new ExpenseDTO();
//        dto.setId(entity.getId());
        dto.setDate(entity.getDate());
        dto.setAmount(entity.getAmount());
        dto.setDescription(entity.getDescription());
        dto.setSharedBetween(entity.getSharedBetween());
        dto.setCreditCardId(entity.getCreditCard().getId());
        dto.setBankAccountIds(entity.getBankAccounts().stream()
                .map(BankAccount::getId)
                .collect(Collectors.toList()));
        return dto;
    }

    public Expense fromDto(ExpenseDTO dto) {
        Expense entity = new Expense();
        entity.setDate(dto.getDate());
        entity.setAmount(dto.getAmount());
        entity.setDescription(dto.getDescription());
        entity.setSharedBetween(dto.getSharedBetween());
        return entity;
    }

    public void updateEntityFromDto(Expense entity, ExpenseDTO dto) {
        entity.setDate(dto.getDate());
        entity.setAmount(dto.getAmount());
        entity.setDescription(dto.getDescription());
        entity.setSharedBetween(dto.getSharedBetween());
    }
}
