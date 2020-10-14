package com.symphony.bdk.app.spring.exception;

public class AppAuthException extends RuntimeException {

  public AppAuthException(String message, Exception e) {
    super(message, e);
  }

  public AppAuthException(String message) {
    super(message);
  }
}
