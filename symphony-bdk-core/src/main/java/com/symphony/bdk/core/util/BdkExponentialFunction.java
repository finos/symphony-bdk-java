package com.symphony.bdk.core.util;

import com.symphony.bdk.core.config.model.BdkRetryConfig;
import io.github.resilience4j.core.IntervalFunction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BdkExponentialFunction {

    public static IntervalFunction ofExponentialBackoff(BdkRetryConfig retryConfig) {
        long initialIntervalMillis = retryConfig.getInitialIntervalMillis();
        double multiplier = retryConfig.getMultiplier();
        long maxIntervalMillis = retryConfig.getMaxIntervalMillis();
        if (multiplier < 1.0D) {
            throw new IllegalArgumentException("Illegal argument multiplier: " + multiplier);
        }
        if (initialIntervalMillis < 10L) {
            throw new IllegalArgumentException("Illegal argument interval: " + initialIntervalMillis + " millis");
        }
        return IntervalFunction.of(initialIntervalMillis, x -> {
            if (multiplier * x <= maxIntervalMillis) {
                return (long) (multiplier * x);
            } else {
                return maxIntervalMillis;
            }
        });
    }
}
