package com.credit_card_bill_tracker.backend.spendingprofile;

import com.credit_card_bill_tracker.backend.bankaccount.BankAccountRepository;
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

    public List<SpendingProfileDTO> getAll(User user) {
        return repository.findByUserIdAndDeletedFalse(user.getId()).stream()
                .map(mapper::toDto)
                .toList();
    }

    public SpendingProfileDTO create(User user, SpendingProfileDTO dto) {
        SpendingProfile profile = mapper.fromDto(dto);
        profile.setUser(user);
        profile.setBankAccounts(dto.getBankAccountIds().stream()
                .map(id -> bankAccountRepo.findById(id).orElseThrow())
                .collect(Collectors.toList()));
        return mapper.toDto(repository.save(profile));
    }

    public SpendingProfileDTO update(User user, UUID id, SpendingProfileDTO dto) {
        SpendingProfile entity = repository.findById(id)
                .filter(p -> p.getUser().getId().equals(user.getId()))
                .orElseThrow();
        mapper.updateEntityFromDto(entity, dto);
        entity.setBankAccounts(dto.getBankAccountIds().stream()
                .map(bankAccountRepo::findById)
                .map(Optional::orElseThrow)
                .collect(Collectors.toList()));
        return mapper.toDto(repository.save(entity));
    }

    public void delete(User user, UUID id) {
        SpendingProfile profile = repository.findById(id)
                .filter(p -> p.getUser().getId().equals(user.getId()))
                .orElseThrow();
        profile.setDeleted(true);
        repository.save(profile);
    }
}