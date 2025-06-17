package com.credit_card_bill_tracker.backend.bankaccount;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {
    List<BankAccount> findByUserIdAndDeletedFalse(UUID userId);
    boolean existsByUserIdAndIsDefaultTrueAndDeletedFalse(UUID userId);
}