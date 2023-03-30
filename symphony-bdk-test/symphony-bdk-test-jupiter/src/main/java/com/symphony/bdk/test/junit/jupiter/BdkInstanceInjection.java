package com.symphony.bdk.test.junit.jupiter;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.test.SymphonyBdkTestMock;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Field;

public class BdkInstanceInjection implements TestInstancePostProcessor {

  private final SymphonyBdkTestMock bdkTestMock;

  public BdkInstanceInjection(SymphonyBdkTestMock bdkTestMock) {
    this.bdkTestMock = bdkTestMock;
  }

  @Override
  public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
    final Class<?> clz = testInstance.getClass();

    for (Field field : clz.getDeclaredFields()) {
      if (SymphonyBdk.class.isAssignableFrom(field.getType())) {
        field.setAccessible(true);
        field.set(testInstance, this.bdkTestMock.getSymphonyBdk());
        field.setAccessible(false);
      }
    }
  }
}
