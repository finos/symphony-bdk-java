package com.symphony.bdk.spring.config;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.service.datafeed.DatafeedService;
import com.symphony.bdk.core.service.datafeed.DatafeedVersion;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.core.service.datafeed.impl.DatafeedServiceV1;
import com.symphony.bdk.core.service.datafeed.impl.DatafeedServiceV2;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.spring.SymphonyBdkCoreProperties;
import com.symphony.bdk.spring.service.DatafeedAsyncLauncherService;

import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * Injection of the {@link DatafeedService} instance into the Spring application context.
 */
public class BdkDatafeedConfig {

  @Bean
  public DatafeedVersion datafeedVersion(SymphonyBdkCoreProperties properties) {
    return DatafeedVersion.of(properties.getDatafeed().getVersion());
  }

  @Bean
  public DatafeedService datafeedService(
      SymphonyBdkCoreProperties properties,
      DatafeedApi datafeedApi,
      AuthSession botSession,
      DatafeedVersion datafeedVersion
  ) {

    if (datafeedVersion == DatafeedVersion.V2) {
      return new DatafeedServiceV2(datafeedApi, botSession, properties);
    }

    return new DatafeedServiceV1(datafeedApi, botSession, properties);
  }

  @Bean(initMethod = "start", destroyMethod = "stop")
  public DatafeedAsyncLauncherService datafeedAsyncLauncherService(final DatafeedService datafeedService, List<RealTimeEventListener> realTimeEventListeners) {
    return new DatafeedAsyncLauncherService(datafeedService, realTimeEventListeners);
  }
}
