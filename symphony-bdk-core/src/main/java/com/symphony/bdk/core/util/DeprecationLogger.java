package com.symphony.bdk.core.util;

import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to log deprecation message using "deprecation" logger
 */
@API(status = API.Status.INTERNAL)
public final class DeprecationLogger {

  private static final Logger log = LoggerFactory.getLogger("deprecation");

  private DeprecationLogger() {
    // to avoid instantiation
  }

  /**
   * Logs a WARN deprecation message
   *
   * @param message deprecation message to log.
   */
  public static void logDeprecation(String message) {
    log.warn(message);
  }
}
