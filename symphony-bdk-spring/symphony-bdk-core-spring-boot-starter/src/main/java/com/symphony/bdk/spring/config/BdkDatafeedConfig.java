package com.symphony.bdk.spring.config;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.service.datafeed.DatafeedLoop;
import com.symphony.bdk.core.service.datafeed.DatafeedVersion;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.core.service.datafeed.impl.DatafeedLoopV1;
import com.symphony.bdk.core.service.datafeed.impl.DatafeedLoopV2;
import com.symphony.bdk.core.service.session.SessionService;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.spring.SymphonyBdkCoreProperties;
import com.symphony.bdk.spring.service.DatafeedAsyncLauncherService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * Injection of the {@link DatafeedLoop} instance into the Spring application context.
 */
@ConditionalOnProperty(value = "bdk.datafeed.enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnBean(name = "botSession")
public class BdkDatafeedConfig {

  @Bean
  @ConditionalOnMissingBean
  public DatafeedVersion datafeedVersion(SymphonyBdkCoreProperties properties) {
    return DatafeedVersion.of(properties.getDatafeed().getVersion());
  }

  @Bean("datafeedLoop")
  @ConditionalOnMissingBean
  public DatafeedLoop datafeedLoop(
      SymphonyBdkCoreProperties properties,
      @Qualifier("datafeedApi") DatafeedApi datafeedApi,
      AuthSession botSession,
      DatafeedVersion datafeedVersion,
      SessionService sessionService
  ) {

    if (datafeedVersion == DatafeedVersion.V2) {
      return new DatafeedLoopV2(datafeedApi, botSession, properties, sessionService.getSession());
    }

    return new DatafeedLoopV1(datafeedApi, botSession, properties, sessionService.getSession());
  }

  @Bean(initMethod = "start", destroyMethod = "stop")
  @ConditionalOnMissingBean
  public DatafeedAsyncLauncherService datafeedAsyncLauncherService(@Qualifier("datafeedLoop") DatafeedLoop datafeedLoop, List<RealTimeEventListener> realTimeEventListeners) {
    return new DatafeedAsyncLauncherService(datafeedLoop, realTimeEventListeners);
  }
}
