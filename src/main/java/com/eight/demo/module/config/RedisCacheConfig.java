package com.eight.demo.module.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "redis")
@Configuration
public class RedisCacheConfig {

    @Getter
    @Setter
    public static class Wrapper {
        private String cacheName;
        private Duration ttl;
    }

    private List<Wrapper> caches;
    private final Map<String, Wrapper> cacheMap = new HashMap<>();

    public Duration getTTL(String cacheName) {
        if (cacheMap.isEmpty()) {
            synchronized (cacheMap) {
                cacheMap.putAll(caches.stream().collect(Collectors.toMap(Wrapper::getCacheName, Function.identity())));
            }
        }
        var find = cacheMap.get(cacheName);
        if (find != null) {
            return find.getTtl();
        }
        return Duration.ZERO;
    }
}
