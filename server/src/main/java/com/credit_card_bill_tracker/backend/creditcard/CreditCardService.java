package com.credit_card_bill_tracker.backend.creditcard;

import com.credit_card_bill_tracker.backend.common.errors.ResourceNotFoundException;
import com.credit_card_bill_tracker.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreditCardService {

    private final CreditCardRepository repository;
    private final CreditCardMapper mapper;

    public List<CreditCardResponseDTO> getAll(User user) {
        return repository.findByUserId(user.getId()).stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    public CreditCardResponseDTO create(User user, CreditCardDTO dto) {
        CreditCard card = mapper.fromDto(dto);
        card.setUser(user);
        return mapper.toResponseDto(repository.save(card));
    }

    public CreditCardResponseDTO update(User user, UUID id, CreditCardDTO dto) {
        CreditCard card = repository.findById(id)
                .filter(c -> c.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Credit card not found"));

        mapper.updateEntityFromDto(card, dto);
        return mapper.toResponseDto(repository.save(card));
    }

    public void deleteCard(UUID id) {
        CreditCard card = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Credit card not found"));
        card.softDelete();
        repository.save(card);
    }
}
