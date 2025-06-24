package com.credit_card_bill_tracker.backend.spendingprofile;

import com.credit_card_bill_tracker.backend.common.BaseRepository;

import java.util.List;
import java.util.UUID;

public interface SpendingProfileRepository extends BaseRepository<SpendingProfile, UUID> {
    List<SpendingProfile> findByUserId(UUID userId);
}