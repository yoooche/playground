package com.eight.demo.module.cache;

import java.time.Duration;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisHelper {

    private final RedisTemplate<String, Object> redisTemplate;

    public long incrementWithCustomTTL(String cacheName, long ttl, Object... keys) {
        var key = getCacheKey(cacheName, keys);
        var count = redisTemplate.opsForValue().increment(key);

        if (count != null && count == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(ttl));
        }
        return count != null ? count : 0L;
    }

    private String getCacheKey(String cacheName, Object... args) {
        if (args == null || args.length == 0) {
            return cacheName;
        }
        return cacheName.concat("::").concat(StringUtils.join(args, ":"));
    }
}
