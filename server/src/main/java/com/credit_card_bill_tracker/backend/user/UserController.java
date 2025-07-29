package com.credit_card_bill_tracker.backend.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserDTO dto) {
        UserResponseDTO result = userService.getProfile(userService.register(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Get user profile", description = "Retrieves the authenticated user's profile or a user by id")
    @GetMapping({"", "/{id}"})
    public ResponseEntity<UserResponseDTO> getUserById(@AuthenticationPrincipal User currentUser, @PathVariable(required = false) UUID id) {
        UserResponseDTO result;
        if (id != null) {
            result = userService.getProfile(userService.getById(id));
        } else {
            result = userService.getProfile(currentUser);
        }
        return ResponseEntity.ok(result);
    }
}
