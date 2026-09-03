package com.app.banking.redis;

import com.app.banking.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;

@Component
public class AuthenticatedUserRateLimitInterceptor implements HandlerInterceptor {
    private final RedisRateLimiter redisRateLimiter;
    private static final int MAX_REQUESTS_PER_MIN = 30;

    public AuthenticatedUserRateLimitInterceptor(RedisRateLimiter redisTemplate) {
        this.redisRateLimiter = redisTemplate;
    }

    // Check rates of authenticated user
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws  Exception{
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // if not authenticated, return
        if(auth == null || !(auth.getPrincipal() instanceof CustomUserDetails principal)) {
            return true;
        }

        Long userId = principal.getId();
        String key = "ratelimit:user:" + userId;

        boolean allowed = redisRateLimiter.isAllowed(key, MAX_REQUESTS_PER_MIN, Duration.ofMinutes(1));

        if(allowed == false) {
            response.setStatus(429); // too many requests
            response.getWriter().write("Rate limit exceeded. Try again in a minute.");
            return false; // blocks request from hitting controller
        }
        return true; // allows request to proceed

    }
}
