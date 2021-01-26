package com.symphony.bdk.examples.kafka.config.condition;

import com.symphony.bdk.examples.kafka.config.BotConfigurationProperties;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class ProducerCondition implements Condition {

  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    return context.getEnvironment().getProperty(BotConfigurationProperties.PREFIX + ".isProducer").equals("true");
  }
}
