package com.credit_card_bill_tracker.backend.spendingprofile;

import com.credit_card_bill_tracker.backend.bankaccount.BankAccountRepository;
import com.credit_card_bill_tracker.backend.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SpendingProfileService {

    private final SpendingProfileRepository profileRepo;
    private final BankAccountRepository bankAccountRepo;

    public List<SpendingProfile> getUserProfiles(UUID userId) {
        return profileRepo.findByUserIdAndDeletedFalse(userId);
    }

    @Transactional
    public SpendingProfile create(User user, SpendingProfileDTO dto) {
        SpendingProfile profile = new SpendingProfile();
        profile.setUser(user);
        profile.setName(dto.getName());
        profile.setBankAccounts(bankAccountRepo.findAllById(dto.getBankAccountIds()));
        return profileRepo.save(profile);
    }

    public void delete(UUID id) {
        SpendingProfile profile = profileRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        profile.setDeleted(true);
        profileRepo.save(profile);
    }
}