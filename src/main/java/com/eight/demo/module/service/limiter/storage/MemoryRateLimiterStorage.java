package com.eight.demo.module.service.limiter.storage;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

import com.eight.demo.module.to.limiter.LeakyBucket;
import com.eight.demo.module.to.limiter.TokenBucket;

@Component("memoryRateLimiterStorage")
public class MemoryRateLimiterStorage implements RateLimiterStorage {

    private static final ConcurrentHashMap<String, Counter> storage = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final ConcurrentHashMap<String, ConcurrentSkipListMap<Long, AtomicLong>> slidingWindowStorage = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, TokenBucket> tokenBucketStorage = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, LeakyBucket> leakyBucketStorage = new ConcurrentHashMap<>();
    private static final int SLIDING_WIN_BUCKET_SIZE_SECS = 10;

    public MemoryRateLimiterStorage() {
        scheduler.scheduleAtFixedRate(this::cleanExpireKey, 30, 30, TimeUnit.SECONDS);
    }

    @Override
    public long incrementAndSetExpire(String key, long expireSeconds) {
        var expireTime = System.currentTimeMillis() + expireSeconds * 1000;
        var counter = storage.compute(key, (k, existing) -> {
            if (existing == null || existing.isExpired()) {
                return new Counter(new AtomicLong(1), expireTime);
            } else {
                existing.count.incrementAndGet();
                return existing;
            }
        });
        return counter.count.longValue();
    }

    @Override
    public long addToSlidingWindow(String key, long timestamp, int windowSeconds) {
        var bucketTimestamp = (timestamp / SLIDING_WIN_BUCKET_SIZE_SECS) * SLIDING_WIN_BUCKET_SIZE_SECS;
        var timeSeries = slidingWindowStorage.computeIfAbsent(key, k -> new ConcurrentSkipListMap<>());
        var bucketCount = timeSeries.computeIfAbsent(bucketTimestamp, k -> new AtomicLong(0));

        bucketCount.incrementAndGet();

        var expireTime = timestamp - windowSeconds;
        timeSeries.headMap(expireTime).clear();
        return getCountInSlidingWindow(key, expireTime, timestamp);
    }

    @Override
    public long getCountInSlidingWindow(String key, long windownStartTime, long windowEndTime) {
        var timeSeries = slidingWindowStorage.get(key);
        if (timeSeries == null) {
            return 0;
        }

        return timeSeries.subMap(windownStartTime, true, windowEndTime, true)
                .values()
                .stream()
                .mapToLong(AtomicLong::get)
                .sum();
    }

    @Override
    public void cleanupExpiredWindows(String key, long expireTime) {
        var timeSeries = slidingWindowStorage.get(key);
        if (timeSeries != null) {
            timeSeries.headMap(expireTime).clear();
            if (timeSeries.isEmpty()) {
                slidingWindowStorage.remove(key);
            }
        }
    }

    @Override
    public boolean tryConsumeToken(String key, int capacity, int refillRate) {
        var bucket = tokenBucketStorage.computeIfAbsent(key, k -> new TokenBucket(capacity, refillRate));

        synchronized (bucket) {
            refillTokens(bucket);
            if (bucket.getTokens() >= 1) {
                bucket.setTokens(bucket.getTokens() - 1);
                return true;
            }
        }
        return false;
    }

    private void refillTokens(TokenBucket bucket) {
        var now = System.currentTimeMillis();
        var elapsedTime = now - bucket.getLastRefillTime();

        if (elapsedTime > 0) {
            var tokensToAdd = (elapsedTime / 1000.0) * bucket.getRefillRate();
            var newTokens = Math.min(bucket.getCapacity(), bucket.getTokens() + tokensToAdd);
            bucket.setTokens(newTokens);
            bucket.setLastRefillTime(now);
        }
    }

    private void cleanExpireKey() {
        var currentTime = System.currentTimeMillis() / 1000;
        var expireTime = currentTime - 1000;
        var beforeOneHour = System.currentTimeMillis() - 3600000;

        // fixed window
        storage.entrySet().removeIf(entry -> entry.getValue().isExpired());

        // sliding window
        slidingWindowStorage.forEach((key, timeSeries) -> {
            timeSeries.headMap(expireTime).clear();
        });
        slidingWindowStorage.entrySet().removeIf((entry -> entry.getValue().isEmpty()));

        // token bucket
        tokenBucketStorage.entrySet().removeIf(entry -> {
            var bucket = entry.getValue();
            return bucket.getLastRefillTime() < beforeOneHour;
        });

        // leacky bucket
        leakyBucketStorage.entrySet().removeIf(entry -> {
            var bucket = entry.getValue();
            return bucket.getLastLeakTime() < beforeOneHour && bucket.getCurrentTasks() == 0;
        });

    }

    private static class Counter {
        private final AtomicLong count;
        private final long expireTime;

        public Counter(AtomicLong count, long expireTime) {
            this.count = count;
            this.expireTime = expireTime;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
    }

    @Override
    public boolean tryAddToLeakyBucket(String key, int capacity, double leakRate) {
        var bucket = leakyBucketStorage.computeIfAbsent(key, k -> new LeakyBucket(capacity, leakRate));

        synchronized (bucket) {
            leakFromBucket(bucket);
            if (bucket.getCurrentTasks() < bucket.getCapacity()) {
                bucket.setCurrentTasks(bucket.getCurrentTasks() + 1);
                return true;
            }
        }
        return false;
    }

    private void leakFromBucket(LeakyBucket bucket) {
        var now = System.currentTimeMillis();
        var elapsedTime = now - bucket.getLastLeakTime();

        if (elapsedTime > 0) {
            var leakTasks = (elapsedTime / 1000.0) * bucket.getLeakRate();
            var newTasks = Math.max(0, bucket.getCurrentTasks() - leakTasks);
            bucket.setCurrentTasks(newTasks);
            bucket.setLastLeakTime(now);
        }
    }
}
