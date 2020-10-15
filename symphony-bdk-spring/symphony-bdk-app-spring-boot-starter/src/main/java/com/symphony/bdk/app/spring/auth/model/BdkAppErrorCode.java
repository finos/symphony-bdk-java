package com.symphony.bdk.app.spring.auth.model;

/**
 * Error Code explains explicitly the error returned by the Extension App Backend.
 */
public enum BdkAppErrorCode {

  UNAUTHORIZED("Failed to authenticate the extension app {appId}."),
  INVALID_TOKEN("Failed to validate the app token."),
  INVALID_JWT("Failed to validate the jwt."),
  MISSING_FIELDS("Some fields in the request body are missing.");

  private final String message;

  BdkAppErrorCode(String message) {
    this.message = message;
  }

  public String getMessage() {
    return this.message;
  }
}
