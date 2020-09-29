package com.symphony.bdk.core.config.exception;

import org.apiguardian.api.API;

/**
 * Thrown when unable to load {@link com.symphony.bdk.core.config.model.BdkConfig} object from the {@link com.symphony.bdk.core.config.BdkConfigLoader}.
 */
@API(status = API.Status.STABLE)
public class BdkConfigException extends Exception {

  public BdkConfigException(String message) {
    super(message);
  }

  public BdkConfigException(String message, Throwable cause) {
    super(message, cause);
  }
}
