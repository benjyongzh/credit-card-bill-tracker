package com.credit_card_bill_tracker.backend.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public UserResponseDTO register(@Valid @RequestBody UserDTO dto) {
        return userService.getProfile(userService.register(dto));
    }

    @GetMapping("/testget")
    public UserResponseDTO getTestUser(@AuthenticationPrincipal User user) {
        return userService.getProfile(user);
    }
}
