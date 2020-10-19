package com.symphony.bdk.app.spring.exception;

import static java.util.Collections.singletonList;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Error can be returned by the Extension App Backend
 */
@Getter
@Setter
public class BdkAppError {

  /**
   * {@link BdkAppErrorCode} of the Extension App Error
   */
  private BdkAppErrorCode code;

  /**
   * Error message will be returned
   */
  private List<String> message;

  public static BdkAppError fromException(BdkAppException e) {
    final BdkAppError bdkAppError = new BdkAppError();
    bdkAppError.setCode(e.getErrorCode());
    String errorMessage = e.getErrorCode().getMessage();
    for (String errorMessageParam : e.getParams()) {
      errorMessage = errorMessage.replaceFirst("\\{}", errorMessageParam);
    }
    bdkAppError.setMessage(singletonList(errorMessage));
    return bdkAppError;
  }
}
