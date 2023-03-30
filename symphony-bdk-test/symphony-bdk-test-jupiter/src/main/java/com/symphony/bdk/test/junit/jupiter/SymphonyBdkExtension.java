package com.symphony.bdk.test.junit.jupiter;

import com.symphony.bdk.test.SymphonyBdkTestMock;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

public class SymphonyBdkExtension implements Extension, BeforeAllCallback, ParameterResolver,
    TestInstancePostProcessor {
  private final SymphonyBdkTestMock bdkTestMock = new SymphonyBdkTestMock();
  private final BdkTestInitialisation initDelegate = new BdkTestInitialisation(bdkTestMock);
  private final BdkParameterResolver resolverDelegate = new BdkParameterResolver(bdkTestMock);

  private final BdkInstanceInjection injectionDelegate = new BdkInstanceInjection(bdkTestMock);

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    initDelegate.beforeAll(context);
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return resolverDelegate.supportsParameter(parameterContext, extensionContext);
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return resolverDelegate.resolveParameter(parameterContext, extensionContext);
  }

  @Override
  public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
    injectionDelegate.postProcessTestInstance(testInstance, context);
  }
}
