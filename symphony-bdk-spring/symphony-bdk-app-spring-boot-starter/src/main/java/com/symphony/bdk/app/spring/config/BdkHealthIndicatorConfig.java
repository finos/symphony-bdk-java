package com.symphony.bdk.app.spring.config;

import com.symphony.bdk.app.spring.service.SymphonyBdkHealthIndicator;
import com.symphony.bdk.core.service.health.HealthService;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;


@ConditionalOnProperty("bdk.bot.username")
public class BdkHealthIndicatorConfig {

  @Bean(name = "bot")
  @ConditionalOnMissingBean
  public SymphonyBdkHealthIndicator symphonyBdkHealthIndicator(HealthService healthService) {
    return new SymphonyBdkHealthIndicator(healthService);
  }

}
