package com.credit_card_bill_tracker.backend.user;

import com.credit_card_bill_tracker.backend.common.ApiResponse;
import com.credit_card_bill_tracker.backend.common.ApiResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Register user", description = "Creates a new user account")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> register(@Valid @RequestBody UserDTO dto) {
        UserResponseDTO result = userService.getProfile(userService.register(dto));
        return ApiResponseBuilder.created(result);
    }

    @Operation(summary = "Get user profile", description = "Retrieves the authenticated user's profile or a user by id")
    @GetMapping({"", "/{id}"})
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(@AuthenticationPrincipal User currentUser, @PathVariable(required = false) UUID id) {
        UserResponseDTO result;
        if (id != null) {
            result = userService.getProfile(userService.getById(id));
        } else {
            result = userService.getProfile(currentUser);
        }
        return ApiResponseBuilder.ok(result);
    }
}
