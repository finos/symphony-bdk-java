package com.symphony.bdk.examples.kafka;

import com.symphony.bdk.examples.kafka.config.BotConfigurationProperties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(BotConfigurationProperties.class)
public class BotApplication {

  public static void main(String[] args) {
    SpringApplication.run(BotApplication.class, args);
  }
}
