package com.app.banking.config;

import com.app.banking.redis.AuthenticatedUserRateLimitInterceptor;
import com.app.banking.redis.PublicRateLimitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final AuthenticatedUserRateLimitInterceptor authUserRateLimiterInterceptor;
    private final PublicRateLimitInterceptor pubRateLimiterInterceptor;

    public WebConfig(AuthenticatedUserRateLimitInterceptor authUserRateLimiterInterceptor, PublicRateLimitInterceptor pubRateLimiterInterceptor) {
        this.authUserRateLimiterInterceptor = authUserRateLimiterInterceptor;
        this.pubRateLimiterInterceptor = pubRateLimiterInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(pubRateLimiterInterceptor)
                .addPathPatterns(
                        "/auth/register",
                        "/auth/login"
                );

        registry.addInterceptor(authUserRateLimiterInterceptor)
                .addPathPatterns(
                        "/accounts/**",
                        "/transactions/**"
                );
    }
}
