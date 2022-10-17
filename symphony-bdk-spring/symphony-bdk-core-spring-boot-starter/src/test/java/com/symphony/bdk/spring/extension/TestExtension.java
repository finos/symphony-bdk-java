package com.symphony.bdk.spring.extension;

import com.symphony.bdk.extension.BdkExtension;

import com.symphony.bdk.extension.BdkExtensionServiceProvider;

import org.springframework.stereotype.Component;

@Component
public class TestExtension implements BdkExtension, BdkExtensionServiceProvider<TestExtensionService> {

  private final TestExtensionService service = new TestExtensionService();

  @Override
  public TestExtensionService getService() {
    return this.service;
  }
}
