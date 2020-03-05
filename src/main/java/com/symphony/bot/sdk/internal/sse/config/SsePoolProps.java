package com.symphony.bot.sdk.internal.sse.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "concurrency.sse.pool")
public class SsePoolProps {

  private Integer coreSize;

  private Integer maxSize;

  private Integer queueCapacity;

  private String threadNamePrefix;

}
