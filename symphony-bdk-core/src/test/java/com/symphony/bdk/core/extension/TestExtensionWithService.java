package com.symphony.bdk.core.extension;

import com.symphony.bdk.extension.BdkExtension;
import com.symphony.bdk.extension.BdkExtensionServiceProvider;

public class TestExtensionWithService implements BdkExtension,
    BdkExtensionServiceProvider<TestExtensionWithService.TestExtensionService> {

  public static class TestExtensionService {}

  @Override
  public TestExtensionService getService() {
    return new TestExtensionService();
  }
}
