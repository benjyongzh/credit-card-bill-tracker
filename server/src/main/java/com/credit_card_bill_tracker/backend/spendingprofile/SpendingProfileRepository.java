package com.credit_card_bill_tracker.backend.spendingprofile;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpendingProfileRepository extends JpaRepository<SpendingProfile, UUID> {
    List<SpendingProfile> findByUserIdAndDeletedFalse(UUID userId);
}