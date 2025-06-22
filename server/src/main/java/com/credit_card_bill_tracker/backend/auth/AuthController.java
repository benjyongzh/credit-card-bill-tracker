package com.credit_card_bill_tracker.backend.auth;

import com.credit_card_bill_tracker.backend.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(@Valid @RequestBody AuthRequestDTO request) {
        String token = authService.login(request.getUsername(), request.getPassword());
        AuthResponseDTO result = new AuthResponseDTO(token);
        ApiResponse<AuthResponseDTO> response = new ApiResponse<>(true, "Login successful", result);
        return ResponseEntity.ok(response);
    }
}
