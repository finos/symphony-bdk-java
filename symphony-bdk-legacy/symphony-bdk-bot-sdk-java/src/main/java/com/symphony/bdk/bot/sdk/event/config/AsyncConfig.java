package com.symphony.bdk.bot.sdk.event.config;

import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.symphony.bdk.bot.sdk.commons.MDCTaskDecorator;

/**
 * Defines thread pool configuration to run event and command handlers
 * asynchronously.
 *
 * @author Marcus Secato
 *
 */
@Configuration
@EnableAsync
public class AsyncConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger(AsyncConfig.class);

  private final BotPoolProps poolProps;

  public AsyncConfig(BotPoolProps poolProps) {
    this.poolProps = poolProps;
  }

  /**
   * ThreadPoolExecutor to handle events and commands in asynchronously
   * @return {@link Executor}
   */
  @Bean(name="botTaskExecutor")
  public Executor botTaskExecutor() {
    LOGGER.info("Initializing bot thread pool");
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
