package com.symphony.bdk.core.util.function;

import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.api.invoker.ApiRuntimeException;

import org.apiguardian.api.API;

/**
 * Functional interface which supplies a T object and may throw an {@link ApiException}.
 *
 * @param <T> the type returned by the supplier.
 */
@FunctionalInterface
@API(status = API.Status.INTERNAL)
public interface SupplierWithApiException<T> {
  T get() throws ApiException;
}
