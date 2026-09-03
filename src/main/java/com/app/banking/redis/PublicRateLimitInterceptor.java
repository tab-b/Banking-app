package com.app.banking.redis;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;

@Component
public class PublicRateLimitInterceptor implements HandlerInterceptor {
    private final RedisRateLimiter rateLimiter;
    private Long maxRequests = 20L;

    public PublicRateLimitInterceptor(RedisRateLimiter r) {
        rateLimiter = r;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        String ip = request.getRemoteAddr();
        String key = "rate:public:ip:" + ip;

        boolean allowed = rateLimiter.isAllowed(key, maxRequests, Duration.ofMinutes(1));
        if(!allowed) {
            response.setStatus(429);
            response.getWriter().write("Too many requests");
            return false;
        }
        return true;
    }
}
