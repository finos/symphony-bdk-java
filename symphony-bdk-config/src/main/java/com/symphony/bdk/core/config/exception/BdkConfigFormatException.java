package com.symphony.bdk.core.config.exception;

import org.apiguardian.api.API;

/**
 * Thrown when a configuration field is not located at the right place in the YAML tree.
 * @see com.symphony.bdk.core.config.model.BdkSslConfig
 */
@API(status = API.Status.STABLE)
public class BdkConfigFormatException extends RuntimeException {

  public BdkConfigFormatException(String message) {
    super(message);
  }

}
