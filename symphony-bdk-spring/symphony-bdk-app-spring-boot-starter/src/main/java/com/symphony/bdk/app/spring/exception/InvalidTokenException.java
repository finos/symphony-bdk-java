package com.symphony.bdk.app.spring.exception;

/**
 * Thrown when the App token is not valid.
 */
public class InvalidTokenException extends RuntimeException {

  public InvalidTokenException(String message) {
    super(message);
  }
}
