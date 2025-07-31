package com.credit_card_bill_tracker.backend.billingcycle;

import com.credit_card_bill_tracker.backend.billingcycle.BillingCycleRequestDTO;
import com.credit_card_bill_tracker.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BillingCycleMapper {

    public BillingCycle toEntity(BillingCycleRequestDTO dto, User user) {
        BillingCycle entity = new BillingCycle();
        entity.setUser(user);
        entity.setLabel(dto.getLabel());
        entity.setMonth(dto.getMonth());
        return entity;
    }

    public void updateEntity(BillingCycle entity, BillingCycleRequestDTO dto) {
        entity.setLabel(dto.getLabel());
        entity.setMonth(dto.getMonth());
    }

    public BillingCycleResponseDTO toResponseDTO(BillingCycle entity) {
        BillingCycleResponseDTO dto = new BillingCycleResponseDTO();
        dto.setId(entity.getId());
        dto.setLabel(entity.getLabel());
        dto.setMonth(entity.getMonth());
        dto.setCompletedDate(entity.getCompletedDate());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
