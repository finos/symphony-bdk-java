package com.symphony.bdk.core.config.model;

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apiguardian.api.API;

@Setter
@NoArgsConstructor
@API(status = API.Status.STABLE)
public class BdkRetryConfig {

  public static final int INFINITE_MAX_ATTEMPTS = -1;
  public static final int DEFAULT_MAX_ATTEMPTS = 10;
  public static final long DEFAULT_INITIAL_INTERVAL_MILLIS = 500L;
  public static final double DEFAULT_MULTIPLIER = 2;
  public static final long DEFAULT_MAX_INTERVAL_MILLIS = 5 * 60 * 1000;

  private Integer maxAttempts;
  private Long initialIntervalMillis;
  private Double multiplier;
  private Long maxIntervalMillis;

  public BdkRetryConfig(Integer maxAttempts) {
    this.maxAttempts = maxAttempts;
  }

  public Integer getMaxAttempts() {

    if (this.maxAttempts == null) {
      return DEFAULT_MAX_ATTEMPTS;
    }

    if (this.maxAttempts < 0) {
      // negative value means infinite number of attempts
      return Integer.MAX_VALUE;
    }

    return this.maxAttempts;
  }

  public Long getInitialIntervalMillis() {
    
    if (this.initialIntervalMillis == null) {
      return DEFAULT_INITIAL_INTERVAL_MILLIS;
    }
    
    return this.initialIntervalMillis;
  }

  public Double getMultiplier() {

    if (this.multiplier == null || this.multiplier < 1) {
      return DEFAULT_MULTIPLIER;
    }
    return this.multiplier;
  }

  public Long getMaxIntervalMillis() {

    if (this.maxIntervalMillis == null) {
      return DEFAULT_MAX_INTERVAL_MILLIS;
    }

    return this.maxIntervalMillis;
  }
}
