package com.symphony.bdk.http.api.tracing;

import lombok.RequiredArgsConstructor;
import org.apiguardian.api.API;
import org.slf4j.MDC;

import java.util.Map;

/**
 * Helper class for managing {@link MDC} in multi-thread applications.
 */
@API(status = API.Status.STABLE)
public final class MDCUtils {

  private MDCUtils() {
    // nothing to be done here
  }

  /**
   * Wrap parent {@link MDC} context in child {@link Runnable}. Ensure that {@link MDC} values set in child runnable don't
   * leak in parent context.
   *
   * @param runnable A simple runnable interface
   * @return the wrapped runnable
   */
  public static Runnable wrap(Runnable runnable) {
    return new MDCUtils.MdcRunnable(runnable);
  }

  private static Map<String, String> beforeCallMDCSetup(Map<String, String> parentContext) {
    final Map<String, String> childContext = MDC.getCopyOfContextMap();
    if (parentContext != null) {
      if (childContext != null) {
        parentContext.putAll(childContext);
      }

      MDC.setContextMap(parentContext);
    }

    return childContext;
  }

  private static void afterCallMDCCleanUp(Map<String, String> childContext) {
    MDC.clear();
    if (childContext != null) {
      MDC.setContextMap(childContext);
    }
  }

  @RequiredArgsConstructor
  private static class MdcRunnable implements Runnable {

    private final Map<String, String> parentContext = MDC.getCopyOfContextMap();
    private final Runnable runnable;

    public void run() {
      final Map<String, String> childContext = MDCUtils.beforeCallMDCSetup(this.parentContext);
      try {
        this.runnable.run();
      } finally {
        MDCUtils.afterCallMDCCleanUp(childContext);
      }
    }
  }
}
