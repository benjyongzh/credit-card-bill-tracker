package com.credit_card_bill_tracker.backend.user;

import com.credit_card_bill_tracker.backend.common.ApiResponse;
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

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> register(@Valid @RequestBody UserDTO dto) {
        UserResponseDTO result = userService.getProfile(userService.register(dto));
        ApiResponse<UserResponseDTO> response = new ApiResponse<>(true, "User registered successfully", result);
        return ResponseEntity.ok(response);
    }

    @GetMapping({"", "/{id}"})
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(@AuthenticationPrincipal User currentUser, @PathVariable(required = false) UUID id) {
        UserResponseDTO result;
        if (id != null) {
            result = userService.getProfile(userService.getById(id));
        } else {
            result = userService.getProfile(currentUser);
        }
        ApiResponse<UserResponseDTO> response = new ApiResponse<>(true, "User profile retrieved successfully", result);
        return ResponseEntity.ok(response);
    }
}
