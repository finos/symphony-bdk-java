package com.symphony.bdk.core.api.invoker.util;

import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.api.invoker.ApiRuntimeException;

@FunctionalInterface
public interface SupplierWithApiException<T> {
  T get() throws ApiException;

  static <T> T callAndCatchApiException(SupplierWithApiException<T> supplier) {
    try {
      return supplier.get();
    } catch (ApiException e) {
      throw new ApiRuntimeException(e);
    }
  }
}
