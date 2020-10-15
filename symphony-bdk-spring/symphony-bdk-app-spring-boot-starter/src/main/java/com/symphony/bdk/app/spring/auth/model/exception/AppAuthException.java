package com.symphony.bdk.app.spring.auth.model.exception;

import com.symphony.bdk.app.spring.auth.model.BdkAppErrorCode;

/**
 * Thrown when the extension app authentication failed.
 */
public class AppAuthException extends RuntimeException {

  private final BdkAppErrorCode errorCode;

  public AppAuthException(Exception e, BdkAppErrorCode errorCode) {
    super(errorCode.getMessage(), e);
    this.errorCode = errorCode;
  }

  public AppAuthException(BdkAppErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public BdkAppErrorCode getErrorCode() {
    return this.errorCode;
  }
}
