package com.credit_card_bill_tracker.backend.spendingprofile;

import com.credit_card_bill_tracker.backend.common.ApiResponse;
import com.credit_card_bill_tracker.backend.common.ApiResponseBuilder;
import com.credit_card_bill_tracker.backend.user.User;
import com.credit_card_bill_tracker.backend.spendingprofile.SpendingProfileRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Get spending profiles", description = "Returns all spending profiles for the authenticated user")
    @GetMapping
    public ResponseEntity<ApiResponse<List<SpendingProfileResponseDTO>>> getAll(@AuthenticationPrincipal User user) {
        List<SpendingProfileResponseDTO> result = service.getAll(user);
        return ApiResponseBuilder.ok(result);
    }

    @Operation(summary = "Create spending profile", description = "Creates a new spending profile")
    @PostMapping
    public ResponseEntity<ApiResponse<SpendingProfileResponseDTO>> create(@AuthenticationPrincipal User user, @RequestBody SpendingProfileRequestDTO dto) {
        SpendingProfileResponseDTO result = service.create(user, dto);
        return ApiResponseBuilder.created(result);
    }

    @Operation(summary = "Update spending profile", description = "Updates the specified spending profile")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SpendingProfileResponseDTO>> update(@AuthenticationPrincipal User user, @PathVariable UUID id, @RequestBody SpendingProfileRequestDTO dto) {
        SpendingProfileResponseDTO result = service.update(user, id, dto);
        return ApiResponseBuilder.accepted(result);
    }

    @Operation(summary = "Delete spending profile", description = "Removes the spending profile identified by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        service.delete(user, id);
        return ApiResponseBuilder.noContent();
    }
}
