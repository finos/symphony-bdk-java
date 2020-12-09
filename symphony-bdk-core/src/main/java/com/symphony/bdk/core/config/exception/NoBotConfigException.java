package com.symphony.bdk.core.config.exception;

import org.apiguardian.api.API;

/**
 * Thrown when the bot configuration is not specified. The BDK will only be runnable in OBO mode.
 */
@API(status = API.Status.STABLE)
public class NoBotConfigException extends RuntimeException {

  public NoBotConfigException(String message) {
    super(message);
  }
}
