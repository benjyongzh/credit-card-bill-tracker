package com.credit_card_bill_tracker.backend.spendingprofile;

import com.credit_card_bill_tracker.backend.bankaccount.BankAccountRepository;
import com.credit_card_bill_tracker.backend.common.errors.ResourceNotFoundException;
import com.credit_card_bill_tracker.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpendingProfileService {

    private final SpendingProfileRepository repository;
    private final SpendingProfileMapper mapper;
    private final BankAccountRepository bankAccountRepo;

    public List<SpendingProfileResponseDTO> getAll(User user) {
        return repository.findByUserIdAndDeletedFalse(user.getId()).stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    public SpendingProfileResponseDTO create(User user, SpendingProfileDTO dto) {
        SpendingProfile profile = mapper.fromDto(dto);
        profile.setUser(user);
        profile.setBankAccounts(dto.getBankAccountIds().stream()
                .map(id -> bankAccountRepo.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Bank account not found")))
                .collect(Collectors.toList()));
        return mapper.toResponseDto(repository.save(profile));
    }

    public SpendingProfileResponseDTO update(User user, UUID id, SpendingProfileDTO dto) {
        SpendingProfile entity = repository.findById(id)
                .filter(p -> p.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Spending profile not found"));
        mapper.updateEntityFromDto(entity, dto);
        entity.setBankAccounts(dto.getBankAccountIds().stream()
                .map(bankAccountRepo::findById)
                .map(optional -> optional.orElseThrow(() -> new ResourceNotFoundException("Bank account not found")))
                .collect(Collectors.toList()));
        return mapper.toResponseDto(repository.save(entity));
    }

    public void delete(User user, UUID id) {
        SpendingProfile profile = repository.findById(id)
                .filter(p -> p.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Spending profile not found"));
        profile.setDeleted(true);
        repository.save(profile);
    }
}
