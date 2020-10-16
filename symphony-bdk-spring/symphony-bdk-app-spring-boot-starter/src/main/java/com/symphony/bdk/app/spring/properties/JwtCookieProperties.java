package com.symphony.bdk.app.spring.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import javax.validation.constraints.Min;

/**
 * Configuration Properties for storing Jwt in cookie for the Extension App
 */
@Getter
@Setter
public class JwtCookieProperties {

  private static final Integer DEFAULT_MAX_AGE = 24*60*60;

  private Boolean enabled = false;

  @DurationUnit(ChronoUnit.SECONDS)
  @Min(value = 0)
  private Duration maxAge = Duration.ofSeconds(DEFAULT_MAX_AGE);
}
