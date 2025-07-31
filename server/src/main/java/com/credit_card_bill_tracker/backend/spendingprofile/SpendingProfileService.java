package com.credit_card_bill_tracker.backend.spendingprofile;

import com.credit_card_bill_tracker.backend.bankaccount.BankAccountRepository;
import com.credit_card_bill_tracker.backend.common.errors.ResourceNotFoundException;
import com.credit_card_bill_tracker.backend.common.errors.BadRequestException;
import com.credit_card_bill_tracker.backend.user.User;
import com.credit_card_bill_tracker.backend.spendingprofile.SpendingProfileRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpendingProfileService {

    private final SpendingProfileRepository repository;
    private final SpendingProfileMapper mapper;
    private final BankAccountRepository bankAccountRepo;

    public List<SpendingProfileResponseDTO> getAll(User user) {
        return repository.findByUserId(user.getId()).stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    public SpendingProfileResponseDTO create(User user, SpendingProfileRequestDTO dto) {
        if (repository.existsByUserIdAndName(user.getId(), dto.getName())) {
            throw new BadRequestException("Spending profile with that name already exists");
        }
        SpendingProfile profile = mapper.fromDto(dto);
        profile.setUser(user);
        profile.setBankAccounts(dto.getBankAccountIds().stream()
                .map(id -> bankAccountRepo.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Bank account not found")))
                .collect(Collectors.toList()));
        return mapper.toResponseDto(repository.save(profile));
    }

    public SpendingProfileResponseDTO update(User user, UUID id, SpendingProfileRequestDTO dto) {
        SpendingProfile entity = repository.findById(id)
                .filter(p -> p.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Spending profile not found"));
        if (repository.existsByUserIdAndNameAndIdNot(user.getId(), dto.getName(), id)) {
            throw new BadRequestException("Spending profile with that name already exists");
        }
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
        profile.softDelete();
        repository.save(profile);
    }
}
