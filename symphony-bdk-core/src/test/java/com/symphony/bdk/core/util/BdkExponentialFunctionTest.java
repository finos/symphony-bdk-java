package com.symphony.bdk.core.util;

import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.core.retry.util.BdkExponentialFunction;
import io.github.resilience4j.core.IntervalFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BdkExponentialFunctionTest {

    @Test
    void ofExponentialBackoff() {
        BdkRetryConfig retryConfig = new BdkRetryConfig();
        retryConfig.setMultiplier(1.5);
        retryConfig.setInitialIntervalMillis(100L);
        IntervalFunction function = BdkExponentialFunction.ofExponentialBackoff(retryConfig);
        Long interval = function.apply(2);
        assertEquals(interval, 150L);
    }

    @Test
    void invalidMultiplier() {
        assertThrows(IllegalArgumentException.class, () -> {
            BdkRetryConfig retryConfig = new BdkRetryConfig();
            retryConfig.setInitialIntervalMillis(9L);
            BdkExponentialFunction.ofExponentialBackoff(retryConfig);
        });
    }

    @Test
    void maximumInterval() {
        BdkRetryConfig retryConfig = new BdkRetryConfig();
        retryConfig.setMultiplier(1.5);
        retryConfig.setInitialIntervalMillis(100L);
        retryConfig.setMaxIntervalMillis(200L);
        IntervalFunction function = BdkExponentialFunction.ofExponentialBackoff(retryConfig);
        Long interval = function.apply(3);
        assertEquals(interval, 200L);
    }
}
