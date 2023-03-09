package com.symphony.bdk.test;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("bot")
@Data
public class BdkSpringTestProperties {
  private Long id;
  private String username;
  private String displayName;
  private String email;
}
