package com.credit_card_bill_tracker.backend.creditcard;

import org.springframework.stereotype.Component;

@Component
public class CreditCardMapper {

    public CreditCardDTO toDto(CreditCard entity) {
        CreditCardDTO dto = new CreditCardDTO();
        dto.setId(entity.getId());
        dto.setCardName(entity.getCardName());
        dto.setLastFourDigits(entity.getLastFourDigits());
        return dto;
    }

    public CreditCard fromDto(CreditCardDTO dto) {
        CreditCard entity = new CreditCard();
        entity.setCardName(dto.getCardName());
        entity.setLastFourDigits(dto.getLastFourDigits());
        return entity;
    }

    public void updateEntityFromDto(CreditCard entity, CreditCardDTO dto) {
        entity.setCardName(dto.getCardName());
        entity.setLastFourDigits(dto.getLastFourDigits());
    }
}
