package com.symphony.bdk.core.util.function;


import com.symphony.bdk.http.api.ApiException;

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
