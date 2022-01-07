package com.symphony.bdk.core.config.legacy.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apiguardian.api.API;

/**
 * Sub-configuration class for Retry parametrization. Experimental, the contract might change in the future.
 *
 * <p>
 *   This class will have to be removed in the future.
 * </p>
 */
@ToString
@Getter @Setter
@Deprecated
@API(status = API.Status.DEPRECATED)
public class LegacyRetryConfiguration {

    public static final int DEFAULT_MAX_ATTEMPTS = 10;
    public static final long DEFAULT_INITIAL_INTERVAL_MILLIS = 500L;
    public static final double DEFAULT_MULTIPLIER = 1.5;

    private int maxAttempts = DEFAULT_MAX_ATTEMPTS;
    private long initialIntervalMillis = DEFAULT_INITIAL_INTERVAL_MILLIS;
    private double multiplier = DEFAULT_MULTIPLIER;
}
