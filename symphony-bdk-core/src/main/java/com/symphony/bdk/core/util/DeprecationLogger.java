package com.symphony.bdk.core.util;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.util.Objects;

@Slf4j
@API(status = API.Status.INTERNAL)
/**
 * Class to log deprecation message except if system property bdk.warning.mode is set to none.
 */
public class DeprecationLogger {

  private static final boolean shouldLogDeprecationMessages = !Objects.equals(System.getProperty("bdk.warning.mode"),
      "none");

  /**
   * Logs a WARN message except if system property bdk.warning.mode=none
   *
   * @param message deprecation message to log.
   */
  public static void logDeprecation(String message) {
    if (shouldLogDeprecationMessages) {
      log.warn(message);
    }
  }
}
