package com.symphony.bdk.core.test;

/**
 * Exception returned by {@link MockApiClient}.
 */
public class MockApiClientException extends RuntimeException {

  public MockApiClientException(String message) {
    super(message);
  }
}
