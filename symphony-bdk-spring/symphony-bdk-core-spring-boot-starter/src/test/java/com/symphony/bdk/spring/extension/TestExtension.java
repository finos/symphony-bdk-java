package com.symphony.bdk.spring.extension;

import com.symphony.bdk.extension.BdkExtension;
import com.symphony.bdk.extension.BdkExtensionServiceProvider;

import org.springframework.stereotype.Component;

@Component
public class TestExtension implements BdkExtension, BdkExtensionServiceProvider<TestExtensionService> {

  @Override
  public TestExtensionService getService() {
    return new TestExtensionService();
  }
}
