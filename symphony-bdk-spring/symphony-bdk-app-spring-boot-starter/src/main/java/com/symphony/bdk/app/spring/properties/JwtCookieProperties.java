package com.symphony.bdk.app.spring.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Configuration Properties for storing Jwt in cookie for the Extension App
 */
@Getter
@Setter
public class JwtCookieProperties {

  private static final Integer DEFAULT_MAX_AGE = 24*60*60;

  /**
   * The flag to enable to store the JWT in cookie.
   */
  private Boolean enabled = false;

  /**
   * The maximum duration that the JWT will be stored in cookie.
   */
  @DurationUnit(ChronoUnit.SECONDS)
  private Duration maxAge = Duration.ofSeconds(DEFAULT_MAX_AGE);
}
