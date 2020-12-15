package com.symphony.bdk.core.util;

import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to log deprecation message using "deprecation" logger
 */
@API(status = API.Status.INTERNAL)
public final class DeprecationLogger {

  public static final String LOGGER_NAME = "com.symphony.bdk.deprecation";
  private static final Logger log = LoggerFactory.getLogger(LOGGER_NAME);

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
