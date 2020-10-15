package com.symphony.bdk.app.spring.auth.model;

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
}
