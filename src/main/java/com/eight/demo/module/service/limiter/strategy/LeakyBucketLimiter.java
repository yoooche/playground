package com.eight.demo.module.service.limiter.strategy;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.eight.demo.module.common.annotation.RateLimiter;
import com.eight.demo.module.constant.Algorithm;
import com.eight.demo.module.service.limiter.storage.RateLimiterStorage;

@Component
public class LeakyBucketLimiter implements RateLimiterStrategy {

    private final RateLimiterStorage rateLimiterStorage;

    public LeakyBucketLimiter(@Qualifier("memoryRateLimiterStorage") RateLimiterStorage rateLimiterStorage) {
        this.rateLimiterStorage = rateLimiterStorage;
    }

    @Override
    public boolean isAllow(String key, RateLimiter rateLimiter) {
        var capacity = rateLimiter.limit();
        var leakRate = (double) rateLimiter.limit() / rateLimiter.window();
        return rateLimiterStorage.tryAddToLeakyBucket(key, capacity, leakRate);
    }

    @Override
    public Algorithm getAlgorithmType() {
        return Algorithm.LEAKY_BUCKET;
    }

}
