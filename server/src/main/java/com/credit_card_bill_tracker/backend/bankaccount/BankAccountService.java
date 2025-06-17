package com.credit_card_bill_tracker.backend.bankaccount;

import com.credit_card_bill_tracker.backend.creditcard.CreditCard;
import com.credit_card_bill_tracker.backend.creditcard.CreditCardRepository;
import com.credit_card_bill_tracker.backend.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BankAccountService {

    private final BankAccountRepository bankAccountRepo;
    private final CreditCardRepository creditCardRepo;
    private final BankAccountMapper mapper;

    public List<BankAccountResponseDTO> getAll(User user) {
        return bankAccountRepo.findByUserIdAndDeletedFalse(user.getId()).stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Transactional
    public BankAccountResponseDTO create(User user, BankAccountDTO dto) {
        BankAccount account = mapper.fromDto(dto);
        account.setUser(user);

        if (dto.getDefaultCardId() != null) {
            CreditCard card = creditCardRepo.findById(dto.getDefaultCardId())
                    .orElseThrow(() -> new RuntimeException("Card not found"));
            account.setDefaultCard(card);
        }
        if (dto.isDefault() && bankAccountRepo.existsByUserIdAndIsDefaultTrueAndDeletedFalse(user.getId())) {
            List<BankAccount> existingAccounts = bankAccountRepo.findByUserIdAndDeletedFalse(user.getId());
            for (BankAccount acc : existingAccounts) {
                acc.setIsDefault(false);
            }
            bankAccountRepo.saveAll(existingAccounts);
            bankAccountRepo.save(account);
            BankAccountResponseDTO response = mapper.toResponseDto(account);
            response.setChangedOtherAccountsToNotDefault(true);
            return response;
        } else {
            return mapper.toResponseDto(bankAccountRepo.save(account));
        }
    }

    public BankAccountResponseDTO update(User user, UUID id, BankAccountDTO dto) {
        BankAccount account = bankAccountRepo.findById(id)
                .filter(a -> a.getUser().getId().equals(user.getId()))
                .orElseThrow();

        mapper.updateEntityFromDto(account, dto);

        if (dto.getDefaultCardId() != null) {
            CreditCard card = creditCardRepo.findById(dto.getDefaultCardId())
                    .orElseThrow(() -> new RuntimeException("Card not found"));
            account.setDefaultCard(card);
        } else {
            account.setDefaultCard(null);
        }

        return mapper.toResponseDto(bankAccountRepo.save(account));
    }

    public void delete(User user, UUID id) {
        BankAccount account = bankAccountRepo.findById(id)
                .filter(a -> a.getUser().getId().equals(user.getId()))
                .orElseThrow();
        account.setDeleted(true);
        bankAccountRepo.save(account);
    }
}