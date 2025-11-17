package com.eight.demo.module.to.limiter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenBucket {

    private final int capacity;
    private final int refillRate;
    private double tokens;
    private long lastRefillTime;

    public TokenBucket(int capacity, int refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.tokens = capacity;
        this.lastRefillTime = System.currentTimeMillis();
    }
}
