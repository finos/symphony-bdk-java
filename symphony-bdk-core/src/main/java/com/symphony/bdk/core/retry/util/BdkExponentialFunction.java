package com.symphony.bdk.core.retry.util;

import com.symphony.bdk.core.config.model.BdkRetryConfig;
import io.github.resilience4j.core.IntervalFunction;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

/**
 * Custom BdkExponentialFunction class to be used in Bdk Retry mechanism.
 * The interval between 2 retries will be initiated by the value BdkRetryConfig#initialIntervalMillis.
 *
 * After each retry, this interval will be multiplied by BdkRetryConfig#multiplier.
 *
 * This interval will be limited by BdkRetryConfig#maxIntervalMillis, in means that, when this interval
 * is greater than this value, it will not be multiplied any more and set to BdkRetryConfig#maxIntervalMillis.
 */
@Slf4j
@API(status = API.Status.INTERNAL)
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
