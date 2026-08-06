package com.symphony.bdk.core.extension;

import com.symphony.bdk.extension.BdkExtension;
import com.symphony.bdk.extension.BdkExtensionConfigAware;
import com.symphony.bdk.extension.BdkExtensionService;
import com.symphony.bdk.extension.BdkExtensionServiceProvider;

import lombok.Getter;
import lombok.Setter;

public class TestExtensionConfigAware
    implements BdkExtension, BdkExtensionConfigAware<TestExtensionConfigAware.Config>,
    BdkExtensionServiceProvider<TestExtensionConfigAware.ConfigService> {

  @Getter
  @Setter
  public static class Config {
    private String value;
  }

  @Getter
  @Setter
  public static class ConfigService implements BdkExtensionService {
    Config config;
  }

  private final ConfigService service = new ConfigService();

  @Override
  public String getConfigKey() {
    return "test";
  }

  @Override
  public Class<Config> getConfigClass() {
    return Config.class;
  }

  @Override
  public void setExtensionConfig(Config config) {
    this.service.setConfig(config);
  }

  @Override
  public ConfigService getService() {
    return this.service;
  }
}
