package com.credit_card_bill_tracker.backend.auth;

import com.credit_card_bill_tracker.backend.user.User;
import com.credit_card_bill_tracker.backend.user.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Value("${jwt.cookieName}")
    private String jwtCookieName;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.frontend-login-redirect-url}")
    private String redirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        String email = oidcUser.getEmail();
        User user = userRepository.findByEmail(email).orElse(null);
        String username = user != null ? user.getUsername() : email;
        String token = jwtUtil.generateToken(username);
        Cookie cookie = new Cookie(jwtCookieName, token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
        response.sendRedirect(frontendUrl + "/" + redirectUrl);
    }
}
