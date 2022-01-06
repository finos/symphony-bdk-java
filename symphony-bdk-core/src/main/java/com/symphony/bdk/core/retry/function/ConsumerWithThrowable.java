package com.symphony.bdk.core.retry.function;

import org.apiguardian.api.API;

/**
 * Functional interface which may throw a {@link Throwable}.
 * This is used to specify recovery functions in {@link ConsumerWithThrowable}.
 */
@FunctionalInterface
@API(status = API.Status.INTERNAL)
public interface ConsumerWithThrowable {

  void consume() throws Throwable;
}
