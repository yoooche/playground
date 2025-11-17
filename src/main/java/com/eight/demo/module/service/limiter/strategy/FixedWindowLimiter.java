package com.eight.demo.module.service.limiter.strategy;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.eight.demo.module.common.annotation.RateLimiter;
import com.eight.demo.module.constant.Algorithm;
import com.eight.demo.module.service.limiter.storage.RateLimiterStorage;

@Component
public class FixedWindowLimiter implements RateLimiterStrategy {

    private final RateLimiterStorage storage;

    public FixedWindowLimiter(@Qualifier("redisRateLimiterStorage") RateLimiterStorage storage) {
        this.storage = storage;
    }

    @Override
    public boolean isAllow(String key, RateLimiter rateLimiter) {
        var currentCount = storage.incrementAndSetExpire(key, rateLimiter.window());
        return currentCount <= rateLimiter.limit();
    }

    @Override
    public Algorithm getAlgorithmType() {
        return Algorithm.FIXED_WINDOW;
    }
}
