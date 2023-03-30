package com.symphony.bdk.test.junit.jupiter;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.test.SymphonyBdkTestMock;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class BdkParameterResolver implements ParameterResolver {

  private final SymphonyBdkTestMock bdkTestMock;

  public BdkParameterResolver(SymphonyBdkTestMock bdkTestMock) {
    this.bdkTestMock = bdkTestMock;
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(SymphonyBdk.class);
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return bdkTestMock.getSymphonyBdk();
  }
}
