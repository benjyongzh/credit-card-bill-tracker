package com.credit_card_bill_tracker.backend.spendingprofile;

import com.credit_card_bill_tracker.backend.common.ApiResponse;
import com.credit_card_bill_tracker.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<List<SpendingProfileResponseDTO>>> getAll(@AuthenticationPrincipal User user) {
        List<SpendingProfileResponseDTO> result = service.getAll(user);
        ApiResponse<List<SpendingProfileResponseDTO>> response = new ApiResponse<>(true, "Spending profiles retrieved successfully", result);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SpendingProfileResponseDTO>> create(@AuthenticationPrincipal User user, @RequestBody SpendingProfileDTO dto) {
        SpendingProfileResponseDTO result = service.create(user, dto);
        ApiResponse<SpendingProfileResponseDTO> response = new ApiResponse<>(true, "Spending profile created successfully", result);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SpendingProfileResponseDTO>> update(@AuthenticationPrincipal User user, @PathVariable UUID id, @RequestBody SpendingProfileDTO dto) {
        SpendingProfileResponseDTO result = service.update(user, id, dto);
        ApiResponse<SpendingProfileResponseDTO> response = new ApiResponse<>(true, "Spending profile updated successfully", result);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        service.delete(user, id);
        ApiResponse<Void> response = new ApiResponse<>(true, "Spending profile deleted successfully", null);
        return ResponseEntity.ok(response);
    }
}
