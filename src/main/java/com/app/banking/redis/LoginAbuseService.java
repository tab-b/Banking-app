package com.app.banking.redis;

import com.app.banking.exceptions.RateLimitExceededException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Locale;

import static java.util.Objects.hash;

@Service
public class LoginAbuseService {

    private final RedisRateLimiter rateLimiter;

    public LoginAbuseService(RedisRateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    public void checkAccountLimit(String email) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        String key = "rate:login:account:" + hash(normalizedEmail);

        boolean allowed = rateLimiter.isAllowed(
                key,
                5,
                Duration.ofMinutes(5)
        );

        if(!allowed) {
            throw new RateLimitExceededException("Too many login attempts!");
        }
    }
}