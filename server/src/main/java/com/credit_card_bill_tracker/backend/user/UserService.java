package com.credit_card_bill_tracker.backend.user;

import com.credit_card_bill_tracker.backend.bankaccount.BankAccount;
import com.credit_card_bill_tracker.backend.bankaccount.BankAccountRepository;
import com.credit_card_bill_tracker.backend.creditcard.CreditCard;
import com.credit_card_bill_tracker.backend.creditcard.CreditCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CreditCardRepository creditCardRepository;
    private final BankAccountRepository bankAccountRepository;

    private final PasswordEncoder passwordEncoder;

    public User register(UserDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setRole("user");

        user = userRepository.save(user);

        // Create default credit card
        CreditCard card = new CreditCard();
        card.setUser(user);
        card.setCardName("Default Card");
        creditCardRepository.save(card);

        // Create default bank account
        BankAccount account = new BankAccount();
        account.setUser(user);
        account.setName("Default Bank Account");
        account.setIsDefault(true);
        account.setDefaultCard(card);
        bankAccountRepository.save(account);

        return user;
    }

    public UserResponseDTO getProfile(User user) {
        return new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }

    public User getById(UUID id) {
        return userRepository.findById(id).orElseThrow();
    }
}