package com.symphony.bdk.core.config.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BdkRetryConfig {

    public static final int DEFAULT_MAX_ATTEMPTS = 10;
    public static final long DEFAULT_INITIAL_INTERVAL_MILLIS = 500L;
    public static final double DEFAULT_MULTIPLIER = 2;
    public static final long DEFAULT_MAX_INTERVAL_MILLIS = 5*60*1000;

    private int maxAttempts = DEFAULT_MAX_ATTEMPTS;
    private long initialIntervalMillis = DEFAULT_INITIAL_INTERVAL_MILLIS;
    private double multiplier = DEFAULT_MULTIPLIER;
    private long maxIntervalMillis = DEFAULT_MAX_INTERVAL_MILLIS;

    public double getMultiplier() {
        if (this.multiplier < 1) {
            return 1;
        }
        return this.multiplier;
    }
}
