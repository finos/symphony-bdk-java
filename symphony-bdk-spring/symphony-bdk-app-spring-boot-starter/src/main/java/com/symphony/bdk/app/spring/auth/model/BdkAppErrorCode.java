package com.symphony.bdk.app.spring.auth.model;

import org.springframework.http.HttpStatus;

/**
 * Error Code explains explicitly the error returned by the Extension App Backend.
 */
public enum BdkAppErrorCode {

  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Failed to authenticate the extension app {appId}."),
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Failed to validate the app token."),
  INVALID_JWT(HttpStatus.UNAUTHORIZED, "Failed to validate the jwt."),
  MISSING_FIELDS(HttpStatus.BAD_REQUEST, "Some fields in the request body are missing.");

  private final HttpStatus httpStatus;
  private final String message;

  BdkAppErrorCode(HttpStatus httpStatus, String message) {
    this.httpStatus = httpStatus;
    this.message = message;
  }

  public String getMessage() {
    return this.message;
  }

  public HttpStatus getHttpStatus() {
    return this.httpStatus;
  }
}
