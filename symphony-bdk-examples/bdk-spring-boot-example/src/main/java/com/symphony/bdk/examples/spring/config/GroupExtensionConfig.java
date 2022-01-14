package com.symphony.bdk.examples.spring.config;

import com.symphony.bdk.ext.group.SymphonyGroupBdkExtension;

//@Configuration
public class GroupExtensionConfig {

  //@Bean
  public SymphonyGroupBdkExtension groupExtension() {
    return new SymphonyGroupBdkExtension();
  }
}
