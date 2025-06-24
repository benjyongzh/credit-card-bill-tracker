package com.credit_card_bill_tracker.backend.bankaccount;

import com.credit_card_bill_tracker.backend.common.BaseRepository;

import java.util.List;
import java.util.UUID;

public interface BankAccountRepository extends BaseRepository<BankAccount, UUID> {
    List<BankAccount> findByUserId(UUID userId);
    boolean existsByUserIdAndIsDefaultTrue(UUID userId);
}