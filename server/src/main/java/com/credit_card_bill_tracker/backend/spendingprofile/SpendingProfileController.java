package com.credit_card_bill_tracker.backend.spendingprofile;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.credit_card_bill_tracker.backend.user.User;
import com.credit_card_bill_tracker.backend.spendingprofile.SpendingProfileRequestDTO;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Get spending profiles", description = "Returns all spending profiles for the authenticated user")
    @GetMapping
    public ResponseEntity<List<SpendingProfileResponseDTO>> getAll(@AuthenticationPrincipal User user) {
        List<SpendingProfileResponseDTO> result = service.getAll(user);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Create spending profile", description = "Creates a new spending profile")
    @PostMapping
    public ResponseEntity<SpendingProfileResponseDTO> create(@AuthenticationPrincipal User user, @Valid @RequestBody SpendingProfileRequestDTO dto) {
        SpendingProfileResponseDTO result = service.create(user, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Update spending profile", description = "Updates the specified spending profile")
    @PutMapping("/{id}")
    public ResponseEntity<SpendingProfileResponseDTO> update(@AuthenticationPrincipal User user, @PathVariable UUID id, @Valid @RequestBody SpendingProfileRequestDTO dto) {
        SpendingProfileResponseDTO result = service.update(user, id, dto);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(result);
    }

    @Operation(summary = "Delete spending profile", description = "Removes the spending profile identified by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        service.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}
