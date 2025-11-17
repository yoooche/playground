package com.eight.demo.module.service.limiter;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.eight.demo.module.common.error.BaseException;
import com.eight.demo.module.constant.Algorithm;
import com.eight.demo.module.constant.StatusCode;
import com.eight.demo.module.service.limiter.strategy.RateLimiterStrategy;

@Slf4j
@Component
public class RateLimiterFactory {

    private final Map<Algorithm, RateLimiterStrategy> strategies;

    public RateLimiterFactory(List<RateLimiterStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        RateLimiterStrategy::getAlgorithmType,
                        Function.identity()));
    }

    public RateLimiterStrategy getStrategy(Algorithm algorithm) {
        var startegy = strategies.get(algorithm);
        return Optional.ofNullable(startegy)
                .orElseThrow(() -> new BaseException(StatusCode.UNKNOW_ERR, "Rate limiter strategy not found"));
    }

    public Set<Algorithm> getAvailableAlgorithm() {
        return strategies.keySet();
    }
}