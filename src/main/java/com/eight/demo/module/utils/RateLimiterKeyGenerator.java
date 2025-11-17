package com.eight.demo.module.utils;

import com.eight.demo.module.common.annotation.RateLimiter;
import com.eight.demo.module.core.RequestContextHelper;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

@Component
public class RateLimiterKeyGenerator {

    private static final String KEY_SEPARATOR = ":";

    public String generateKey(ProceedingJoinPoint joinPoint, RateLimiter rateLimiter) {
        var baseKey = getBaseKey(joinPoint);
        var contextKey = getContextKey(rateLimiter);

        return String.join(KEY_SEPARATOR, baseKey, contextKey);
    }

    private String getBaseKey(ProceedingJoinPoint joinPoint) {
        var className = joinPoint.getTarget().getClass().getSimpleName();
        var methodName = joinPoint.getSignature().getName();
        return String.join(KEY_SEPARATOR, className, methodName);
    }

    private String getContextKey(RateLimiter rateLimiter) {
        var keyParts = new StringBuilder();
        
        if (rateLimiter.byClientIP()) {
            var clientIp = RequestContextHelper.getClientIp();
            keyParts.append("ip:").append(clientIp != null ? clientIp : "unknown");
        }
        
        return keyParts.toString();
    }

}
