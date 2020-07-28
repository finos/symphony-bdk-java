package com.symphony.bdk.bot.sdk.webapi.throttling.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.symphony.bdk.bot.sdk.webapi.throttling.ThrottlingFilter;
import com.symphony.bdk.bot.sdk.webapi.throttling.ThrottlingModeEnum;

/**
 * Throttling filter configuration
 *
 * @author msecato
 *
 */
@Configuration
public class ThrottlingConfig {

  private static final long THROTTLE_TIMEOUT = 5000L;

  @Bean
  @ConditionalOnProperty(prefix = "throttling", value = "limit")
  public FilterRegistrationBean<ThrottlingFilter> throttlingFilter(Environment env) {
    FilterRegistrationBean<ThrottlingFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(new ThrottlingFilter(
        getLimit(env), getMode(env), getTimeout(env)));

    return registrationBean;
  }

  private int getLimit(Environment env) {
    return Integer.parseInt(env.getProperty("throttling.limit"));
  }

  private ThrottlingModeEnum getMode(Environment env) {
    try {
      return ThrottlingModeEnum.valueOf(env.getProperty("throttling.mode"));
    } catch (Exception e) {
      return ThrottlingModeEnum.ENDPOINT;
    }
  }

  private long getTimeout(Environment env) {
    String timeoutProp = env.getProperty("throttling.timeout");
    if (timeoutProp != null && !timeoutProp.isEmpty()) {
      return Long.parseLong(timeoutProp);
    }
    return THROTTLE_TIMEOUT;
  }

}
