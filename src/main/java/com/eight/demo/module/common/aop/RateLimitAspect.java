package com.eight.demo.module.common.aop;

import com.eight.demo.module.common.annotation.RateLimiter;
import com.eight.demo.module.common.error.BaseException;
import com.eight.demo.module.constant.StatusCode;
import com.eight.demo.module.service.limiter.RateLimiterFactory;
import com.eight.demo.module.utils.RateLimiterKeyGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class RateLimitAspect {

    private final RateLimiterKeyGenerator rateLimiterKeyGenerator;
    private final RateLimiterFactory rateLimiterFactory;

    @Around("@annotation(rateLimiter)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimiter rateLimiter) throws Throwable {
        var key = rateLimiterKeyGenerator.generateKey(joinPoint, rateLimiter);
        var strategy = rateLimiterFactory.getStrategy(rateLimiter.algorithm());
        var isAllowed = strategy.isAllow(key, rateLimiter);

        if (!isAllowed) {
            var method = joinPoint.getSignature().toShortString();
            log.warn("Rate limit exceed for method: {}, key: {}", method, key);
            throw new BaseException(StatusCode.TOO_MANY_REQUEST,
                    String.format("Rate limit exceeded by key: %s. Max %d requests per %d seconds",
                            key, rateLimiter.limit(), rateLimiter.window()));
        }
        log.debug("Rate limit passed for key: {}", key);
        return joinPoint.proceed();
    }
}
