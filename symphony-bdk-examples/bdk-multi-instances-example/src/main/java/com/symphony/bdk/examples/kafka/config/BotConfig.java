package com.symphony.bdk.examples.kafka.config;

import com.symphony.bdk.core.service.datafeed.DatafeedLoop;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.examples.kafka.config.condition.ConsumerCondition;
import com.symphony.bdk.spring.service.DatafeedAsyncLauncherService;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Slf4j
@Configuration
public class BotConfig {

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

  @Bean
  @Conditional(ConsumerCondition.class)
  public DatafeedAsyncLauncherService datafeedAsyncLauncherService() {
    log.info("Datafeed is disabled for consumers.");
    return new DatafeedAsyncLauncherService(new DummyDatafeedLoop(), Collections.emptyList());
  }

  private static class DummyDatafeedLoop implements DatafeedLoop {

    @Override
    public void start() {}

    @Override
    public void stop() {}

    @Override
    public void subscribe(RealTimeEventListener listener) {}

    @Override
    public void unsubscribe(RealTimeEventListener listener) {}
  }
}
