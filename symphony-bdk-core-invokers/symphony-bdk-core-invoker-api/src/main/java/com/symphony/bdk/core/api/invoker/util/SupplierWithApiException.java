package com.symphony.bdk.core.api.invoker.util;

import com.symphony.bdk.core.api.invoker.ApiException;

@FunctionalInterface
public interface SupplierWithApiException<T> {
  T get() throws ApiException;
}
