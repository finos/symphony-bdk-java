package com.symphony.bdk.examples.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.ObjectMapper;

@SpringBootApplication
public class SampleSpringApplication {

  @Bean
  public ObjectMapper jacksonObjectMapper() {
    return new ObjectMapper();
  }

  public static void main(String[] args) {
    SpringApplication.run(SampleSpringApplication.class, args);
  }
}
