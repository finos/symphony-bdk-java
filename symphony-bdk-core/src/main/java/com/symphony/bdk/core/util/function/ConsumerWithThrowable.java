package com.symphony.bdk.core.util.function;

import com.symphony.bdk.core.api.invoker.ApiException;

import org.apiguardian.api.API;

/**
 * Functional interface which consumes an {@link ApiException} and may throw a {@link Throwable}.
 * This is used to specify recovery functions in {@link ConsumerWithThrowable}.
 */
@FunctionalInterface
@API(status = API.Status.INTERNAL)
public interface ConsumerWithThrowable {
  void consume() throws Throwable;
}
