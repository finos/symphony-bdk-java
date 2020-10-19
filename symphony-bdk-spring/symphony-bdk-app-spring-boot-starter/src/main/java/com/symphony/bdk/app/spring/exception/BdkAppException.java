package com.symphony.bdk.app.spring.exception;

import com.symphony.bdk.app.spring.auth.model.BdkAppErrorCode;

/**
 * Thrown when any Extension App Api request is failed.
 */
public class BdkAppException extends RuntimeException {

  private final BdkAppErrorCode errorCode;

  public BdkAppException(BdkAppErrorCode errorCode, Exception e) {
    super(errorCode.getMessage(), e);
    this.errorCode = errorCode;
  }

  public BdkAppException(BdkAppErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public BdkAppErrorCode getErrorCode() {
    return this.errorCode;
  }
}
