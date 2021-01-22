package com.symphony.bdk.examples.kafka.consumer;

import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.message.util.PresentationMLParser;
import com.symphony.bdk.examples.kafka.config.BotConfigurationProperties;
import com.symphony.bdk.examples.kafka.config.condition.ConsumerCondition;
import com.symphony.bdk.gen.api.model.V4MessageSent;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Conditional;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
@RequiredArgsConstructor
@Conditional(ConsumerCondition.class)
public class RealTimeEventsKafkaConsumer {

  private final ObjectMapper objectMapper;
  private final BotConfigurationProperties config;
  private final MessageService messageService;

  @PostConstruct
  public void init() {
    log.info("Consumer '{}' started", this.config.getConsumerId());
  }

  @KafkaListener(topics = "${my-bot.topic}")
  public void listen(ConsumerRecord<?, ?> cr) throws Exception {

    // parse message payload
    final V4MessageSent message = this.objectMapper.readValue((String) cr.value(), V4MessageSent.class);

    // send back text content + consumer id
    final String msg = "Consumer '" + this.config.getConsumerId() + "' received : " + PresentationMLParser.getTextContent(message.getMessage().getMessage());
    this.messageService.send(
        message.getMessage().getStream(),
        "<messageML>" + msg + "</messageML>"
    );
  }
}
