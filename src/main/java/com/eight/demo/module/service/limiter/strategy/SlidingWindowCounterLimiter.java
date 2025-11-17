package com.eight.demo.module.service.limiter.strategy;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.eight.demo.module.common.annotation.RateLimiter;
import com.eight.demo.module.constant.Algorithm;
import com.eight.demo.module.service.limiter.storage.RateLimiterStorage;

@Component
public class SlidingWindowCounterLimiter implements RateLimiterStrategy {

    private final RateLimiterStorage storage;

    public SlidingWindowCounterLimiter(@Qualifier("memoryRateLimiterStorage") RateLimiterStorage storage) {
        this.storage = storage;
    }

    @Override
    public boolean isAllow(String key, RateLimiter rateLimiter) {
        var currentTimestamp = System.currentTimeMillis() / 1000;
        var currentCount = storage.addToSlidingWindow(key, currentTimestamp, rateLimiter.window());
        return currentCount <= rateLimiter.limit();
    }

    @Override
    public Algorithm getAlgorithmType() {
        return Algorithm.SLIDING_WINDOW_COUNTER;
    }

}
