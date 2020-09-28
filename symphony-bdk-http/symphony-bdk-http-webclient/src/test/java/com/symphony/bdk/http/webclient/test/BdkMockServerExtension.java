package com.symphony.bdk.http.webclient.test;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class BdkMockServerExtension implements Extension, BeforeEachCallback, AfterEachCallback, ParameterResolver {

  private final BdkMockServer bdkMockServer;

  public BdkMockServerExtension() {
    this.bdkMockServer = new BdkMockServer();
  }

  @Override
  public void beforeEach(ExtensionContext extensionContext) {
    this.bdkMockServer.start();
  }

  @Override
  public void afterEach(ExtensionContext extensionContext) {
    this.bdkMockServer.stop();
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().equals(BdkMockServer.class);
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return this.bdkMockServer;
  }
}
