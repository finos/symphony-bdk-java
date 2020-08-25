package com.symphony.bdk.core.util;

import com.symphony.bdk.core.config.model.BdkRetryConfig;
import io.github.resilience4j.core.IntervalFunction;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom BdkExponentialFunction class to be used in Bdk Retry mechanism.
 */
@Slf4j
public class BdkExponentialFunction {

    /**
     * Produce an interval function from given {@link BdkRetryConfig}.
     *
     * @param retryConfig given retry configuration.
     *
     * @return an {@link IntervalFunction} to be used in Retry mechanism.
     */
    public static IntervalFunction ofExponentialBackoff(BdkRetryConfig retryConfig) {
        long initialIntervalMillis = retryConfig.getInitialIntervalMillis();
        double multiplier = retryConfig.getMultiplier();
        long maxIntervalMillis = retryConfig.getMaxIntervalMillis();

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
