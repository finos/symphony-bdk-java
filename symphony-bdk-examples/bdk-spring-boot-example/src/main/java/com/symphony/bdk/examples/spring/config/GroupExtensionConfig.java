package com.symphony.bdk.examples.spring.config;

import com.symphony.bdk.core.extension.ExtensionService;
import com.symphony.bdk.ext.group.SymphonyGroupBdkExtension;
import com.symphony.bdk.ext.group.SymphonyGroupService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GroupExtensionConfig {

  @Bean
  public SymphonyGroupBdkExtension groupExtension() {
    return new SymphonyGroupBdkExtension();
  }

  @Bean
  public SymphonyGroupService groupService(ExtensionService extensionService) {
    return extensionService.service(SymphonyGroupBdkExtension.class);
  }
}
