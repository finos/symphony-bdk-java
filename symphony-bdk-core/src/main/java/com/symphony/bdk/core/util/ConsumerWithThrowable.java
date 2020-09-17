package com.symphony.bdk.core.util;

import com.symphony.bdk.core.api.invoker.ApiException;

@FunctionalInterface
public interface ConsumerWithThrowable {
  void get(ApiException e) throws Throwable;
}
