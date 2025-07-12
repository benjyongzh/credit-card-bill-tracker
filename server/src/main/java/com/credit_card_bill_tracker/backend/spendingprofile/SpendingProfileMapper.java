package com.credit_card_bill_tracker.backend.spendingprofile;

import com.credit_card_bill_tracker.backend.bankaccount.BankAccount;
import com.credit_card_bill_tracker.backend.spendingprofile.SpendingProfileRequestDTO;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class SpendingProfileMapper {

    public SpendingProfileRequestDTO toDto(SpendingProfile entity) {
        SpendingProfileRequestDTO dto = new SpendingProfileRequestDTO();
        dto.setName(entity.getName());
        dto.setBankAccountIds(entity.getBankAccounts().stream()
                .map(BankAccount::getId)
                .collect(Collectors.toList()));
        return dto;
    }

    public SpendingProfileResponseDTO toResponseDto(SpendingProfile entity) {
        SpendingProfileResponseDTO dto = new SpendingProfileResponseDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUser().getId());
        dto.setName(entity.getName());
        dto.setBankAccountIds(entity.getBankAccounts().stream()
                .map(BankAccount::getId)
                .collect(Collectors.toList()));
        return dto;
    }

    public SpendingProfile fromDto(SpendingProfileRequestDTO dto) {
        SpendingProfile entity = new SpendingProfile();
        entity.setName(dto.getName());
        return entity;
    }

    public void updateEntityFromDto(SpendingProfile entity, SpendingProfileRequestDTO dto) {
        entity.setName(dto.getName());
    }
}