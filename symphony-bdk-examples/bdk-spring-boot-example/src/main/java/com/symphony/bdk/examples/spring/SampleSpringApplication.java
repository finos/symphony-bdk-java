package com.symphony.bdk.examples.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SampleSpringApplication {

  @Bean
  public ObjectMapper jackson2ObjectMapper() {
    return new ObjectMapper();
  }

  public static void main(String[] args) {
    SpringApplication.run(SampleSpringApplication.class, args);
  }
}
