package com.credit_card_bill_tracker.backend.bankaccount;

import org.springframework.stereotype.Component;

@Component
public class BankAccountMapper {

    public BankAccountDTO toDto(BankAccount entity) {
        BankAccountDTO dto = new BankAccountDTO();
        dto.setName(entity.getName());
        dto.setDefault(entity.isDefault());
        dto.setDefaultCardId(entity.getDefaultCard() != null ? entity.getDefaultCard().getId() : null);
        return dto;
    }

    public BankAccountResponseDTO toResponseDto(BankAccount entity) {
        BankAccountResponseDTO dto = new BankAccountResponseDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUser().getId());
        dto.setName(entity.getName());
        dto.setDefault(entity.isDefault());
        dto.setDefaultCardId(entity.getDefaultCard() != null ? entity.getDefaultCard().getId() : null);
        return dto;
    }

    public BankAccount fromDto(BankAccountDTO dto) {
        BankAccount entity = new BankAccount();
        entity.setName(dto.getName());
        entity.setIsDefault(dto.isDefault());
        // Note: defaultCard should be set in the service via repo lookup.
        return entity;
    }

    public void updateEntityFromDto(BankAccount entity, BankAccountDTO dto) {
        entity.setName(dto.getName());
        entity.setIsDefault(dto.isDefault());
        // Note: defaultCard should be updated in the service via repo lookup.
    }
}