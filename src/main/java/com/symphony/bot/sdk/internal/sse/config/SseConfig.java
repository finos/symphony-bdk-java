package com.symphony.bot.sdk.internal.sse.config;

import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.symphony.bot.sdk.internal.commons.MDCTaskDecorator;

/**
 * Defines thread pool configuration to stream server-sent events
 * asynchronously.
 *
 * @author Marcus Secato
 *
 */
@Configuration
@EnableAsync
public class SseConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger(SseConfig.class);

  private final SsePoolProps poolProps;

  public SseConfig(SsePoolProps poolProps) {
    this.poolProps = poolProps;
  }

  @Bean
  public WebMvcConfigurer webMvcConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor(sseTaskExecutor());
      }
    };
  }

  /**
   * ThreadPoolExecutor used in SSE
   * @return {@link Executor}
   */
  @Bean(name="sseTaskExecutor")
  public ThreadPoolTaskExecutor sseTaskExecutor() {
    LOGGER.info("Initializing SSE thread pool");
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(poolProps.getCoreSize());
    executor.setMaxPoolSize(poolProps.getMaxSize());
    executor.setQueueCapacity(poolProps.getQueueCapacity());
    executor.setThreadNamePrefix(poolProps.getThreadNamePrefix());
    executor.setTaskDecorator(new MDCTaskDecorator());
    executor.initialize();
    return executor;
  }

}
