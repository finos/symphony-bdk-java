package com.symphony.bdk.app.spring.exception;

import static java.util.Collections.emptyList;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * Thrown when any Extension App Api request is failed.
 */
public class BdkAppException extends RuntimeException {

  @Getter private final BdkAppErrorCode errorCode;
  @Getter private final List<String> params;

  public BdkAppException(BdkAppErrorCode errorCode, String... errorMessageParams) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
    this.params = errorMessageParams.length > 0 ? Arrays.asList(errorMessageParams) : emptyList();
  }

  public BdkAppException(BdkAppErrorCode errorCode, Exception e, String... errorMessageParams) {
    super(errorCode.getMessage(), e);
    this.errorCode = errorCode;
    this.params = errorMessageParams.length > 0 ? Arrays.asList(errorMessageParams) : emptyList();
  }
}
