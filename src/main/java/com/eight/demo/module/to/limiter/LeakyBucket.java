package com.eight.demo.module.to.limiter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeakyBucket {

    private final int capacity;
    private final double leakRate;
    private double currentTasks;
    private long lastLeakTime;

    public LeakyBucket(int capacity, double leakRate) {
        this.capacity = capacity;
        this.leakRate = leakRate;
        this.currentTasks = 0;
        this.lastLeakTime = System.currentTimeMillis();
    }
}
