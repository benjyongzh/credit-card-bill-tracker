package com.credit_card_bill_tracker.backend.spendingprofile;

import com.credit_card_bill_tracker.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/spending-profiles")
@RequiredArgsConstructor
public class SpendingProfileController {

    private final SpendingProfileService service;

    @GetMapping
    public List<SpendingProfileDTO> getAll(@AuthenticationPrincipal User user) {
        return service.getAll(user);
    }

    @PostMapping
    public SpendingProfileDTO create(@AuthenticationPrincipal User user, @RequestBody SpendingProfileDTO dto) {
        return service.create(user, dto);
    }

    @PutMapping("/{id}")
    public SpendingProfileDTO update(@AuthenticationPrincipal User user, @PathVariable UUID id, @RequestBody SpendingProfileDTO dto) {
        return service.update(user, id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        service.delete(user, id);
    }
}
