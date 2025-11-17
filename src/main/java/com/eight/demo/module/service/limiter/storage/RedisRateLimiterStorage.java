package com.eight.demo.module.service.limiter.storage;

import org.springframework.stereotype.Component;
import com.eight.demo.module.cache.RedisHelper;
import com.eight.demo.module.constant.RedisKey;

import lombok.RequiredArgsConstructor;

@Component("redisRateLimiterStorage")
@RequiredArgsConstructor
public class RedisRateLimiterStorage implements RateLimiterStorage {

    private final RedisHelper redisHelper;

    @Override
    public long incrementAndSetExpire(String key, long expireSeconds) {
        return redisHelper.incrementWithCustomTTL(RedisKey.RATE_LIMITER.getValue(), expireSeconds, key);
    }

    @Override
    public long addToSlidingWindow(String key, long timestamp, int windowSeconds) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addToSlidingWindow'");
    }

    @Override
    public long getCountInSlidingWindow(String key, long windownStartTime, long windowEndTime) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCountInSlidingWindow'");
    }

    @Override
    public boolean tryConsumeToken(String key, int capacity, int refillRate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'tryConsumeToken'");
    }

    @Override
    public boolean tryAddToLeakyBucket(String key, int capacity, double leakRate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'tryAddToLeakyBucket'");
    }

    @Override
    public void cleanupExpiredWindows(String key, long expireTime) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'cleanupExpiredWindows'");
    }

}
