package com.eight.demo.module.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eight.demo.module.common.annotation.RateLimiter;
import com.eight.demo.module.constant.Algorithm;
import com.eight.demo.module.core.RequestContextHelper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping(value = "/rate")
@RestController
public class RateLimiterTestController {

    @RateLimiter(key = "demo", limit = 5)
    @GetMapping(value = "/window/fixed")
    public ResponseEntity<String> getFixedWindowString() {
        return ResponseEntity.ok("Rate limiter test");
    }

    @RateLimiter(key = "slide", limit = 5, algorithm = Algorithm.SLIDING_WINDOW_COUNTER)
    @GetMapping(value = "/window/sliding/counter")
    public ResponseEntity<String> getSlidingWindowString() {
        return ResponseEntity.ok("Sliding window limiter");
    }

    @RateLimiter(key = "token-bucket", limit = 5, window = 10, algorithm = Algorithm.TOKEN_BUCKET)
    @GetMapping(value = "/token-bucket")
    public ResponseEntity<String> getTokenBucketString() {
        return ResponseEntity.ok("Token Bucket limiter");
    }

    @RateLimiter(key = "leaky-bucket", limit = 5, window = 10, algorithm = Algorithm.LEAKY_BUCKET)
    @GetMapping(value = "/leaky-bucket")
    public ResponseEntity<String> getLeakyBucketString() {
        return ResponseEntity.ok("Leaky Bucket limiter");
    }

    @RateLimiter(limit = 5, window = 10, byClientIP = true)
    @GetMapping(value = "/request-context")
    public ResponseEntity<String> getRequestContextString() {
        return ResponseEntity.ok(RequestContextHelper.getClientIp());
    }
}
