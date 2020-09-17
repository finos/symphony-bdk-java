package com.symphony.bdk.core.util;

import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.api.invoker.ApiRuntimeException;

import org.apiguardian.api.API;

/**
 * Functional interface which supplies a T object and may throw an {@link ApiException}.
 * @param <T> the type returned by the supplier.
 */
@FunctionalInterface
@API(status = API.Status.INTERNAL)
public interface SupplierWithApiException<T> {
  T get() throws ApiException;

  /**
   * Method which wraps {@link #get()} and throws an {@link ApiRuntimeException} if {@link ApiException} is thrown.
   * @param supplier the supplier
   * @param <T> the type returned by the supplier
   * @return the value returned by the supplier
   */
  static <T> T callAndCatchApiException(SupplierWithApiException<T> supplier) {
    try {
      return supplier.get();
    } catch (ApiException e) {
      throw new ApiRuntimeException(e);
    }
  }
}
