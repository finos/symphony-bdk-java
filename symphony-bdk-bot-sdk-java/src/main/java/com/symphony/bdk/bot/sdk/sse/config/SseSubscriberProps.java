package com.symphony.bdk.bot.sdk.sse.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "concurrency.sse.subscriber")
public class SseSubscriberProps {

  private Integer queueCapacity;

  private Long queueTimeout;

}
