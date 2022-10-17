package com.symphony.bdk.spring.config;

import com.symphony.bdk.http.api.tracing.MDCUtils;
import com.symphony.bdk.spring.events.RealTimeEvent;
import com.symphony.bdk.spring.events.RealTimeEventsDispatcher;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@ConditionalOnBean(name = "botSession")
public class BdkCommonFeedConfig {

  @Bean
  @ConditionalOnExpression("'${bdk.datafeed.enabled:true}' == 'true' or '${bdk.datahose.enabled:false}' == 'true'")
  public RealTimeEventsDispatcher realTimeEventsDispatcher(ApplicationEventPublisher publisher) {
    return new RealTimeEventsDispatcher(publisher);
  }

  /**
   * Allows publishing application {@link RealTimeEvent} asynchronously from {@link RealTimeEventsDispatcher}.
   */
  @Bean(name = "applicationEventMulticaster")
  @ConditionalOnExpression("'${bdk.datafeed.event.async:true}' == 'true' or ('${bdk.datahose.enabled:false}' == 'true' and '${bdk.datahose.event.async:true}' == 'true')")
  public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
    final SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
    SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor();
    simpleAsyncTaskExecutor.setTaskDecorator(MDCUtils::wrap);
    eventMulticaster.setTaskExecutor(simpleAsyncTaskExecutor);
    return eventMulticaster;
  }
}
