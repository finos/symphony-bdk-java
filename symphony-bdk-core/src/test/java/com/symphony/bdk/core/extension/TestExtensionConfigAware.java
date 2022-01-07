package com.symphony.bdk.core.extension;

import com.symphony.bdk.core.config.extension.BdkConfigAware;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.extension.BdkExtension;

import com.symphony.bdk.extension.BdkExtensionService;
import com.symphony.bdk.extension.BdkExtensionServiceProvider;

import lombok.Getter;
import lombok.Setter;

public class TestExtensionConfigAware implements BdkExtension, BdkConfigAware, BdkExtensionServiceProvider<TestExtensionConfigAware.ConfigService> {

  @Getter @Setter
  public static class ConfigService implements BdkExtensionService {
    BdkConfig config;
  }

  private final ConfigService service = new ConfigService();

  @Override
  public void setConfiguration(BdkConfig config) {
    this.service.setConfig(config);
  }

  @Override
  public ConfigService getService() {
    return this.service;
  }
}
