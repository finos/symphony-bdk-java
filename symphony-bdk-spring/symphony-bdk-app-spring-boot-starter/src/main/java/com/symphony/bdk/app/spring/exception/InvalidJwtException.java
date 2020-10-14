package com.symphony.bdk.app.spring.exception;

public class InvalidJwtException extends RuntimeException {

  public InvalidJwtException(String message) {
    super(message);
  }

  public InvalidJwtException(String message, Exception e) {
    super(message, e);
  }
}
