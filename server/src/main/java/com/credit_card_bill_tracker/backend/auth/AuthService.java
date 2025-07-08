package com.credit_card_bill_tracker.backend.auth;

import com.credit_card_bill_tracker.backend.common.errors.UnauthorizedException;
import com.credit_card_bill_tracker.backend.user.User;
import com.credit_card_bill_tracker.backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String login(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid username or password");
        }
        String token = jwtUtil.generateToken(user.getUsername());
        log.info("User {} logged in", username);
        return token;
    }

    public void logout(String token) {
        // Stateless JWT: do nothing, or optionally log the logout event
        // If you want to support token revocation, save token to a blacklist
        log.info("Logged out token {}", token);
    }
}
