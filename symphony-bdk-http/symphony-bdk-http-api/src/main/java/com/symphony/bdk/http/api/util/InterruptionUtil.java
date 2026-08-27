package com.symphony.bdk.http.api.util;

import org.apiguardian.api.API;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.concurrent.CancellationException;

/**
 * Utility class to check for thread interruptions and task cancellations.
 */
@API(status = API.Status.INTERNAL)
public final class InterruptionUtil {

  private InterruptionUtil() {
    // Utility class
  }

  /**
   * Represents the level/type of interruption detected.
   */
  public enum InterruptionType {
    /**
     * No interruption or task cancellation detected.
     */
    NONE,

    /**
     * Logical task cancellation detected (e.g. CancellationException).
     */
    INTERRUPTION,

    /**
     * Physical thread interruption detected (e.g. InterruptedException).
     */
    THREAD_INTERRUPTION
  }

  /**
   * Scans the exception's cause chain to identify if it is caused by a thread interruption
   * or a logical task cancellation, with identity-based cycle detection.
   *
   * @param t the throwable to inspect
   * @return the resolved {@link InterruptionType}
   */
  public static InterruptionType getInterruptionType(Throwable t) {
    if (t == null) {
      return InterruptionType.NONE;
    }
    final Set<Throwable> visited = Collections.newSetFromMap(new IdentityHashMap<>());
    Throwable current = t;
    boolean hasCancellation = false;
    while (current != null && visited.add(current)) {
      if (current instanceof InterruptedException) {
        return InterruptionType.THREAD_INTERRUPTION;
      } else if (current instanceof CancellationException) {
        hasCancellation = true;
      }
      current = current.getCause();
    }
    return hasCancellation ? InterruptionType.INTERRUPTION : InterruptionType.NONE;
  }
}
