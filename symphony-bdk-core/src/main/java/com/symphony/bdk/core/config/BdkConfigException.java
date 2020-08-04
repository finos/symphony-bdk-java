package com.symphony.bdk.core.config;

/**
 *
 */
public class BdkConfigException extends RuntimeException {

  /**
   *
   * @param message
   * @param source
   */
  public BdkConfigException(String message, Exception source) {
    super(message, source);
  }
}
