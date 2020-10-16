package com.symphony.bdk.app.spring.auth.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

/**
 * Error can be returned by the Extension App Backend
 */
@Getter
@Setter
public class BdkAppError {

  /**
   * Http Status
   */
  private int status;

  /**
   * {@link BdkAppErrorCode} of the Extension App Error
   */
  private BdkAppErrorCode code;

  /**
   * Error message will be returned
   */
  private List<String> message;

  public static BdkAppError fromBdkAppErrorCode(BdkAppErrorCode errorCode, String appId) {
    BdkAppError bdkAppError = new BdkAppError();
    bdkAppError.setCode(errorCode);
    bdkAppError.setStatus(errorCode.getHttpStatus().value());
    bdkAppError.setMessage(Collections.singletonList(errorCode.getMessage().replace("{appId}", appId)));
    return bdkAppError;
  }
}
