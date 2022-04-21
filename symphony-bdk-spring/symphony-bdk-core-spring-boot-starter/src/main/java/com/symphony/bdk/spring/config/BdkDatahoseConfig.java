package com.symphony.bdk.spring.config;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.service.datafeed.DatahoseLoop;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.core.service.datafeed.impl.DatahoseLoopImpl;
import com.symphony.bdk.core.service.session.SessionService;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.http.api.tracing.MDCUtils;
import com.symphony.bdk.spring.SymphonyBdkCoreProperties;
import com.symphony.bdk.spring.events.RealTimeEvent;
import com.symphony.bdk.spring.events.RealTimeEventsDispatcher;
import com.symphony.bdk.spring.service.DatahoseAsyncLauncherService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.util.List;

@ConditionalOnProperty(value = "bdk.datahose.enabled", havingValue = "true")
@ConditionalOnBean(name = "botSession")
public class BdkDatahoseConfig {

  @Bean("datahoseLoop")
  @ConditionalOnMissingBean
  public DatahoseLoop datahoseLoop(SymphonyBdkCoreProperties properties,
                                       @Qualifier("datahoseApi") DatafeedApi datafeedApi,
                                       AuthSession botSession,
                                       SessionService sessionService) {
    return new DatahoseLoopImpl(datafeedApi, botSession, properties, sessionService.getSession());
  }

  @Bean(initMethod = "start", destroyMethod = "stop")
  public DatahoseAsyncLauncherService datahoseAsyncLauncherService(@Qualifier("datahoseLoop") DatahoseLoop datahoseService,
      List<RealTimeEventListener> realTimeEventListeners) {
    return new DatahoseAsyncLauncherService(datahoseService, realTimeEventListeners);
  }

  /**
   * Allows publishing application {@link RealTimeEvent} asynchronously from {@link RealTimeEventsDispatcher}.
   */
  @Bean(name = "applicationEventMulticaster")
  @ConditionalOnProperty(value = "bdk.datahose.event.async", havingValue = "true", matchIfMissing = true)
  @ConditionalOnMissingBean
  public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
    final SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
    SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor();
    simpleAsyncTaskExecutor.setTaskDecorator(MDCUtils::wrap);
    eventMulticaster.setTaskExecutor(simpleAsyncTaskExecutor);
    return eventMulticaster;
  }
}
