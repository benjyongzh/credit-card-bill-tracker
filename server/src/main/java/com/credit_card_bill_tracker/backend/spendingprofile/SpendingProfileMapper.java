package com.credit_card_bill_tracker.backend.spendingprofile;

import com.credit_card_bill_tracker.backend.bankaccount.BankAccount;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class SpendingProfileMapper {

    public SpendingProfileDTO toDto(SpendingProfile entity) {
        SpendingProfileDTO dto = new SpendingProfileDTO();
        dto.setName(entity.getName());
        dto.setBankAccountIds(entity.getBankAccounts().stream()
                .map(BankAccount::getId)
                .collect(Collectors.toList()));
        return dto;
    }

    public SpendingProfile fromDto(SpendingProfileDTO dto) {
        SpendingProfile entity = new SpendingProfile();
        entity.setName(dto.getName());
        return entity;
    }

    public void updateEntityFromDto(SpendingProfile entity, SpendingProfileDTO dto) {
        entity.setName(dto.getName());
    }
}