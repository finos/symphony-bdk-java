package com.symphony.bdk.core.extension;

import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.extension.BdkExtension;
import com.symphony.bdk.extension.BdkExtensionService;
import com.symphony.bdk.extension.BdkExtensionServiceProvider;

import lombok.Getter;
import lombok.Setter;

public class TestExtensionApiClientFactoryAware implements BdkExtension, BdkApiClientFactoryAware,
    BdkExtensionServiceProvider<TestExtensionApiClientFactoryAware.TestService> {

  @Getter
  @Setter
  public static class TestService implements BdkExtensionService {
    ApiClientFactory apiClientFactory;
  }

  private final TestService service = new TestService();

  @Override
  public void setApiClientFactory(ApiClientFactory apiClientFactory) {
    this.service.setApiClientFactory(apiClientFactory);
  }

  @Override
  public TestService getService() {
    return this.service;
  }
}
