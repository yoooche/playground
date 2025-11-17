package com.eight.demo.module.service.limiter.strategy;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.eight.demo.module.common.annotation.RateLimiter;
import com.eight.demo.module.constant.Algorithm;
import com.eight.demo.module.service.limiter.storage.RateLimiterStorage;

@Component
public class TokenBucketLimiter implements RateLimiterStrategy {

    private final RateLimiterStorage storage;

    public TokenBucketLimiter(@Qualifier("memoryRateLimiterStorage") RateLimiterStorage storage) {
        this.storage = storage;
    }

    @Override
    public boolean isAllow(String key, RateLimiter rateLimiter) {
        var capacity = rateLimiter.limit();
        var refillRate = Math.max(1, rateLimiter.limit() / rateLimiter.window());
        return storage.tryConsumeToken(key, capacity, refillRate);
    }

    @Override
    public Algorithm getAlgorithmType() {
        return Algorithm.TOKEN_BUCKET;
    }

}
