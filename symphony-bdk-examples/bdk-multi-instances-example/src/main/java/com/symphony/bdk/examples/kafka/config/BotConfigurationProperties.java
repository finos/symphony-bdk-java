package com.symphony.bdk.examples.kafka.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = BotConfigurationProperties.PREFIX)
public class BotConfigurationProperties {

  public static final String PREFIX = "my-bot";

  /**
   * bot is producer or consumer
   */
  private Boolean isProducer;
  /**
   * consumer identifier
   */
  private String consumerId;
  /**
   * kafka --> bootstrap servers config
   */
  private String kafkaBootstrapServers;
  /**
   * kafka -> topic name
   */
  private String topic;
  /**
   * kafka -> number of consumers
   */
  private int nbConsumers;
}
