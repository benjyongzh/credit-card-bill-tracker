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

    public List<BankAccount> getUserAccounts(UUID userId) {
        return bankAccountRepo.findByUserIdAndDeletedFalse(userId);
    }

    @Transactional
    public BankAccount createAccount(User user, BankAccountDTO dto) {
        BankAccount account = new BankAccount();
        account.setUser(user);
        account.setName(dto.getName());
        account.setIsDefault(dto.isDefault());

        if (dto.getDefaultCardId() != null) {
            CreditCard card = creditCardRepo.findById(dto.getDefaultCardId())
                    .orElseThrow(() -> new RuntimeException("Card not found"));
            account.setDefaultCard(card);
        }

        if (dto.isDefault() && bankAccountRepo.existsByUserIdAndIsDefaultTrue(user.getId())) {
            throw new RuntimeException("User already has a default account");
        }

        return bankAccountRepo.save(account);
    }

    public void deleteAccount(UUID id) {
        BankAccount account = bankAccountRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Bank account not found"));
        account.setDeleted(true);
        bankAccountRepo.save(account);
    }
}
