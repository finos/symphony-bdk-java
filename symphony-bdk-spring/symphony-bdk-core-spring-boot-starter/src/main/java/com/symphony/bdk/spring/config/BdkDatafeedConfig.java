package com.symphony.bdk.spring.config;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.service.datafeed.DatafeedService;
import com.symphony.bdk.core.service.datafeed.DatafeedVersion;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.core.service.datafeed.impl.DatafeedServiceV1;
import com.symphony.bdk.core.service.datafeed.impl.DatafeedServiceV2;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.spring.SymphonyBdkCoreProperties;
import com.symphony.bdk.spring.events.RealTimeEvent;
import com.symphony.bdk.spring.events.RealTimeEventsDispatcher;
import com.symphony.bdk.spring.service.DatafeedAsyncLauncherService;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.util.List;

/**
 * Injection of the {@link DatafeedService} instance into the Spring application context.
 */
@ConditionalOnProperty(value = "bdk.datafeed.enabled", havingValue = "true", matchIfMissing = true)
public class BdkDatafeedConfig {

  @Bean
  @ConditionalOnMissingBean
  public DatafeedVersion datafeedVersion(SymphonyBdkCoreProperties properties) {
    return DatafeedVersion.of(properties.getDatafeed().getVersion());
  }

  @Bean
  @ConditionalOnMissingBean
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

  @Bean
  @ConditionalOnMissingBean
  public RealTimeEventsDispatcher realTimeEventsDispatcher(ApplicationEventPublisher publisher) {
    return new RealTimeEventsDispatcher(publisher);
  }

  @Bean(initMethod = "start", destroyMethod = "stop")
  @ConditionalOnMissingBean
  public DatafeedAsyncLauncherService datafeedAsyncLauncherService(final DatafeedService datafeedService, List<RealTimeEventListener> realTimeEventListeners) {
    return new DatafeedAsyncLauncherService(datafeedService, realTimeEventListeners);
  }

  /**
   * Allows to publish application {@link RealTimeEvent} asynchronously from {@link RealTimeEventsDispatcher}.
   */
  @Bean(name = "applicationEventMulticaster")
  public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
    final SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
    eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor());
    return eventMulticaster;
  }
}
