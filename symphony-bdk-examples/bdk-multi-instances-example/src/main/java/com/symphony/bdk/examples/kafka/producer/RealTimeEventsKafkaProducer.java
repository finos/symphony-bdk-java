package com.symphony.bdk.examples.kafka.producer;

import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.examples.kafka.config.BotConfigurationProperties;
import com.symphony.bdk.examples.kafka.config.condition.ProducerCondition;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4MessageSent;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
@RequiredArgsConstructor
@Conditional(ProducerCondition.class)
public class RealTimeEventsKafkaProducer implements RealTimeEventListener {

  private final ObjectMapper objectMapper;
  private final KafkaTemplate<String, String> template;
  private final BotConfigurationProperties config;

  @PostConstruct
  public void init() {
    log.info("Producer started");
  }

  @Override
  @SneakyThrows
  public void onMessageSent(V4Initiator initiator, V4MessageSent event) {
    log.info("Producer received Real Time Event, pushing to Kafka...");
    this.template.send(this.config.getTopic(), this.objectMapper.writeValueAsString(event));
  }
}
