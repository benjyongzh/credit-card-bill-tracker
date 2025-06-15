package com.credit_card_bill_tracker.backend.user;

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

    @PostMapping("/register")
    public UserResponseDTO register(@Valid @RequestBody UserDTO dto) {
        return userService.getProfile(userService.register(dto));
    }

    @GetMapping({"", "/{id}"})
    public UserResponseDTO getUserById(@AuthenticationPrincipal User currentUser, @PathVariable(required = false) UUID id) {
        if (id != null) {
            return userService.getProfile(userService.getById(id));
        } else {
            return userService.getProfile(currentUser);
        }
    }
}
