package com.symphony.bdk.examples.kafka.config;

import com.symphony.bdk.examples.kafka.config.condition.ProducerCondition;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Conditional(ProducerCondition.class)
public class BotProducerConfig {

  @Bean
  public KafkaAdmin kafkaAdmin(BotConfigurationProperties properties) {
    final Map<String, Object> configs = new HashMap<>();
    configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getKafkaBootstrapServers());
    return new KafkaAdmin(configs);
  }

  /**
   * Topic configuration
   */
  @Bean
  public NewTopic rteKafkaTopic(BotConfigurationProperties properties) {
    return new NewTopic(properties.getTopic(), properties.getNbConsumers(), (short) 1);
  }
}
