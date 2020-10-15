package com.symphony.bdk.core.config.model;

import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;

@Getter
@Setter
@API(status = API.Status.STABLE)
public class BdkRetryConfig {

    public static final int DEFAULT_MAX_ATTEMPTS = 10;
    public static final long DEFAULT_INITIAL_INTERVAL_MILLIS = 500L;
    public static final double DEFAULT_MULTIPLIER = 2;
    public static final long DEFAULT_MAX_INTERVAL_MILLIS = 5*60*1000;

    private int maxAttempts = DEFAULT_MAX_ATTEMPTS;
    private long initialIntervalMillis = DEFAULT_INITIAL_INTERVAL_MILLIS;
    private double multiplier = DEFAULT_MULTIPLIER;
    private long maxIntervalMillis = DEFAULT_MAX_INTERVAL_MILLIS;

  /**
   * For testing purposes only.
   *
   * @return a new {@link BdkRetryConfig} instance with 10ms interval, multiplier 1, 10 max attempts
   */
  public static BdkRetryConfig ofMinimalInterval() {
      return ofMinimalInterval(DEFAULT_MAX_ATTEMPTS);
    }

  /**
   * For testing purposes only.
   *
   * @param maxAttempts the maximum number
   * @return a new {@link BdkRetryConfig} instance with 10ms interval, multiplier 1
   */
  public static BdkRetryConfig ofMinimalInterval(int maxAttempts) {
    BdkRetryConfig retryConfig = new BdkRetryConfig();
    retryConfig.setMultiplier(1);
    retryConfig.setInitialIntervalMillis(10);
    retryConfig.setMaxIntervalMillis(10);
    retryConfig.setMaxAttempts(maxAttempts);

    return retryConfig;
  }

    public double getMultiplier() {
        if (this.multiplier < 1) {
            return 1;
        }
        return this.multiplier;
    }
}
