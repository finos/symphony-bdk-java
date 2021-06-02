package com.symphony.bdk.spring.config;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.service.datafeed.DatafeedLoop;
import com.symphony.bdk.core.service.datafeed.DatafeedVersion;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.core.service.datafeed.impl.DatafeedLoopV1;
import com.symphony.bdk.core.service.datafeed.impl.DatafeedLoopV2;
import com.symphony.bdk.core.service.session.SessionService;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.spring.SymphonyBdkCoreProperties;
import com.symphony.bdk.spring.events.RealTimeEvent;
import com.symphony.bdk.spring.events.RealTimeEventsDispatcher;
import com.symphony.bdk.spring.service.DatafeedAsyncLauncherService;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

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

  @Bean
  @ConditionalOnMissingBean
  public DatafeedLoop datafeedLoop(
      SymphonyBdkCoreProperties properties,
      DatafeedApi datafeedApi,
      AuthSession botSession,
      DatafeedVersion datafeedVersion,
      SessionService sessionService
  ) {

    final UserV2 botInfo = sessionService.getSession();

    if (datafeedVersion == DatafeedVersion.V2) {
      return new DatafeedLoopV2(datafeedApi, botSession, properties, botInfo);
    }

    return new DatafeedLoopV1(datafeedApi, botSession, properties, botInfo);
  }

  @Bean
  @ConditionalOnMissingBean
  public RealTimeEventsDispatcher realTimeEventsDispatcher(ApplicationEventPublisher publisher) {
    return new RealTimeEventsDispatcher(publisher);
  }

  @Bean(initMethod = "start", destroyMethod = "stop")
  @ConditionalOnMissingBean
  public DatafeedAsyncLauncherService datafeedAsyncLauncherService(final DatafeedLoop datafeedService, List<RealTimeEventListener> realTimeEventListeners) {
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
