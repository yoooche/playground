package com.eight.demo.module.service.limiter.strategy;

import com.eight.demo.module.common.annotation.RateLimiter;
import com.eight.demo.module.constant.Algorithm;

public interface RateLimiterStrategy {

    boolean isAllow(String key, RateLimiter rateLimiter);

    Algorithm getAlgorithmType();
}
