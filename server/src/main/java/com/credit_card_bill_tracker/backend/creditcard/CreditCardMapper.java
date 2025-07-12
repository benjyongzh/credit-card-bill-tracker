package com.credit_card_bill_tracker.backend.creditcard;

import org.springframework.stereotype.Component;

@Component
public class CreditCardMapper {

    public CreditCardRequestDTO toDto(CreditCard entity) {
        CreditCardRequestDTO dto = new CreditCardRequestDTO();
        dto.setCardName(entity.getCardName());
        dto.setLastFourDigits(entity.getLastFourDigits());
        return dto;
    }

    public CreditCardResponseDTO toResponseDto(CreditCard entity) {
        CreditCardResponseDTO dto = new CreditCardResponseDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUser().getId());
        dto.setCardName(entity.getCardName());
        dto.setLastFourDigits(entity.getLastFourDigits());
        return dto;
    }

    public CreditCard fromDto(CreditCardRequestDTO dto) {
        CreditCard entity = new CreditCard();
        entity.setCardName(dto.getCardName());
        entity.setLastFourDigits(dto.getLastFourDigits());
        return entity;
    }

    public void updateEntityFromDto(CreditCard entity, CreditCardRequestDTO dto) {
        entity.setCardName(dto.getCardName());
        entity.setLastFourDigits(dto.getLastFourDigits());
    }
}
