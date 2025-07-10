package com.credit_card_bill_tracker.backend.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {
    private final Map<String, Integer> attempts = new ConcurrentHashMap<>();
    private final Map<String, Long> lastAttempt = new ConcurrentHashMap<>();

    @Value("${login.max-attempts:5}")
    private int maxAttempts;

    @Value("${login.window-ms:60000}")
    private long windowMs;

    public boolean isBlocked(String username) {
        Long last = lastAttempt.get(username);
        if (last == null) {
            return false;
        }
        if (System.currentTimeMillis() - last > windowMs) {
            attempts.remove(username);
            lastAttempt.remove(username);
            return false;
        }
        return attempts.getOrDefault(username, 0) >= maxAttempts;
    }

    public void loginFailed(String username) {
        lastAttempt.put(username, System.currentTimeMillis());
        attempts.merge(username, 1, Integer::sum);
    }

    public void loginSucceeded(String username) {
        attempts.remove(username);
        lastAttempt.remove(username);
    }
}
