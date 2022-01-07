package com.symphony.bdk.examples.spring.group;

import com.symphony.bdk.ext.group.SymphonyGroupBdkExtension;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GroupExtensionConfig {

  @Bean
  public SymphonyGroupBdkExtension symphonyGroupBdkExtension() {
    return new SymphonyGroupBdkExtension();
  }
}
