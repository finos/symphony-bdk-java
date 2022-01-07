package com.symphony.bdk.core.extension;

import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.extension.BdkExtension;
import com.symphony.bdk.extension.BdkExtensionServiceProvider;

import lombok.Getter;
import lombok.Setter;

public class TestExtensionRetryBuilderAware implements BdkExtension, BdkRetryBuilderAware,
    BdkExtensionServiceProvider<TestExtensionRetryBuilderAware.TestService> {

  @Getter
  @Setter
  public static class TestService {
    RetryWithRecoveryBuilder<?> retryBuilder;
  }

  private final TestService service = new TestService();

  @Override
  public void setRetryBuilder(RetryWithRecoveryBuilder<?> retryBuilder) {
    this.service.setRetryBuilder(retryBuilder);
  }

  @Override
  public TestService getService() {
    return this.service;
  }
}
