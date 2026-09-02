package com.app.banking.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisRateLimiter {
    private final StringRedisTemplate redisTemplate;

    public RedisRateLimiter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isAllowed(String key, long maxRequests, Duration window) {
        Long currentRequests = redisTemplate.opsForValue().increment(key);

        if(currentRequests != null && currentRequests == 1) {
            redisTemplate.expire(key, window);
        }

        return currentRequests != null && currentRequests <= maxRequests;
    }
}
