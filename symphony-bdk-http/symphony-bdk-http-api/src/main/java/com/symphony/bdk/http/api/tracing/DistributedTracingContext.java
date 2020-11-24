package com.symphony.bdk.http.api.tracing;

import org.apiguardian.api.API;
import org.slf4j.MDC;

import java.util.Random;

/**
 * Helper class that manipulates underlying logger {@link MDC} for distributed tracing purpose. The Symphony platform
 * internally relies on header <code>X-Trace-Id</code> value that is printed at server side logs. This is especially
 * useful when debugging issues across multiple distributed systems.
 */
@API(status = API.Status.STABLE)
public final class DistributedTracingContext {

  /** {@link Random} is thread-safe */
  private static final Random RANDOM = new Random();

  private static final int TRACE_ID_SIZE = 6;
  private static final char TRACE_ID_SEPARATOR = ':';

  public static final String TRACE_ID = "X-Trace-Id";

  private DistributedTracingContext() {
    // nothing to be done here
  }

  public static void setTraceId() {
    MDC.put(TRACE_ID, randomAlphanumeric(TRACE_ID_SIZE));
  }

  public static void setTraceId(String baseTraceId) {
    MDC.put(TRACE_ID, baseTraceId + TRACE_ID_SEPARATOR + randomAlphanumeric(TRACE_ID_SIZE));
  }

  public static String getTraceId() {
    return MDC.get(TRACE_ID) != null ? MDC.get(TRACE_ID) : "";
  }

  public static boolean hasTraceId() {
    return !getTraceId().isEmpty();
  }

  public static void clear() {
    MDC.remove(TRACE_ID);
  }

  private static String randomAlphanumeric(int size) {

    final int leftLimit = 48; // ascii code for numeral '0'
    final int rightLimit = 122; // ascii code for letter 'z'

    return RANDOM.ints(leftLimit, rightLimit + 1)
        .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
        .limit(size)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }
}
