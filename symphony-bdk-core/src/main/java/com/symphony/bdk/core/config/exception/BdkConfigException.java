package com.symphony.bdk.core.config.exception;

/**
 * Thrown when unable to load {@link com.symphony.bdk.core.config.model.BdkConfig} object from the {@link com.symphony.bdk.core.config.BdkConfigLoader}.
 */
public class BdkConfigException extends Exception {

  public BdkConfigException(String message) {
    super(message);
  }

  public BdkConfigException(String message, Throwable cause) {
    super(message, cause);
  }
}
