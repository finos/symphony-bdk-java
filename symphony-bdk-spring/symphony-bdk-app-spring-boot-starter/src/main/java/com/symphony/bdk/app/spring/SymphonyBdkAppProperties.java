package com.symphony.bdk.app.spring;

import com.symphony.bdk.app.spring.properties.AppAuthProperties;
import com.symphony.bdk.core.config.model.BdkExtAppConfig;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "bdk.app")
public class SymphonyBdkAppProperties extends BdkExtAppConfig {
  private AppAuthProperties auth;
}
