package com.credit_card_bill_tracker.backend.spendingprofile;

import com.credit_card_bill_tracker.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class SpendingProfileController {

    private final SpendingProfileService service;

    @GetMapping
    public List<SpendingProfile> getAll(@AuthenticationPrincipal User user) {
        return service.getUserProfiles(user.getId());
    }

    @PostMapping
    public SpendingProfile create(@AuthenticationPrincipal User user, @RequestBody SpendingProfileDTO dto) {
        return service.create(user, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
