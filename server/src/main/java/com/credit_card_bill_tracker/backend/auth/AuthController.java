package com.credit_card_bill_tracker.backend.auth;

import com.credit_card_bill_tracker.backend.common.ApiResponse;
import com.credit_card_bill_tracker.backend.common.ApiResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Value;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Value("${jwt.cookieName}")
    private String jwtCookieName;

    @Operation(summary = "User login", description = "Validates credentials and sets a JWT token cookie")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(@Valid @RequestBody AuthRequestDTO request,
                                                             HttpServletResponse response) {
        String token = authService.login(request.getUsername(), request.getPassword());
        Cookie cookie = new Cookie(jwtCookieName, token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
        AuthResponseDTO result = new AuthResponseDTO(token);
        return ApiResponseBuilder.ok(result);
    }

    @Operation(summary = "User logout", description = "Invalidates the JWT token cookie")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@CookieValue(value = "${jwt.cookieName}", required = false) String token,
                                                   HttpServletResponse response) {
        if (token != null) {
            authService.logout(token);
        }
        Cookie cookie = new Cookie(jwtCookieName, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ApiResponseBuilder.noContent();
    }
}
