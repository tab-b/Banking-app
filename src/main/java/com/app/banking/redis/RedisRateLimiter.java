package com.app.banking.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class RedisRateLimiter {
    private final StringRedisTemplate redisTemplate;
    private final RedisScript<Long> rateLimitScript;

    public RedisRateLimiter(StringRedisTemplate redisTemplate, RedisScript<Long> rateLimitScript) {
        this.redisTemplate = redisTemplate;
        this.rateLimitScript = rateLimitScript;
    }

    public boolean isAllowed(String key, long maxRequests, Duration window) {
        if(maxRequests <= 0) {
            throw new IllegalArgumentException("maxRequests must be greater than 0");
        }

        if(window.isZero() || window.isNegative()) {
            throw new IllegalArgumentException("window must be greater than 0");
        }
        Long result = redisTemplate.execute(
                rateLimitScript,
                List.of(key),
                String.valueOf(window.toSeconds()),
                String.valueOf(maxRequests)
        );

//        if(currentRequests != null && currentRequests == 1) {
//            redisTemplate.expire(key, window);
//        }

        return result != null && result == 1;
    }
}
