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

    public List<BankAccountDTO> getAll(User user) {
        return bankAccountRepo.findByUserIdAndDeletedFalse(user.getId()).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional
    public BankAccountDTO create(User user, BankAccountDTO dto) {
        BankAccount account = mapper.fromDto(dto);
        account.setUser(user);

        if (dto.getDefaultCardId() != null) {
            CreditCard card = creditCardRepo.findById(dto.getDefaultCardId())
                    .orElseThrow(() -> new RuntimeException("Card not found"));
            account.setDefaultCard(card);
        }

        if (dto.isDefault() && bankAccountRepo.existsByUserIdAndIsDefaultTrue(user.getId())) {
            throw new RuntimeException("User already has a default account");
        }

        return mapper.toDto(bankAccountRepo.save(account));
    }

    public BankAccountDTO update(User user, UUID id, BankAccountDTO dto) {
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

        return mapper.toDto(bankAccountRepo.save(account));
    }

    public void delete(User user, UUID id) {
        BankAccount account = bankAccountRepo.findById(id)
                .filter(a -> a.getUser().getId().equals(user.getId()))
                .orElseThrow();
        account.setDeleted(true);
        bankAccountRepo.save(account);
    }
}