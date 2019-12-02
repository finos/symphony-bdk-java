package com.symphony.ms.songwriter.internal.event.config;

import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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

  private final TaskExecutionProperties taskExecutionProperties;

  public AsyncConfig(TaskExecutionProperties taskExecutionProperties) {
    this.taskExecutionProperties = taskExecutionProperties;
  }

  /**
   * ThreadPoolExecutor to handle events and commands in asynchronously
   * @return {@link Executor}
   */
  @Bean
  public Executor taskExecutor() {
    LOGGER.info("Initializing thread pool");
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(taskExecutionProperties.getPool().getCoreSize());
    executor.setMaxPoolSize(taskExecutionProperties.getPool().getMaxSize());
    executor.setQueueCapacity(taskExecutionProperties.getPool()
        .getQueueCapacity());
    executor.setThreadNamePrefix("sym-thread-pool");
    executor.setTaskDecorator(new MDCTaskDecorator());
    executor.initialize();
    return executor;
  }

}
