package com.symphony.bdk.app.spring.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Error Code explains explicitly the error returned by the Extension App Backend.
 */
@Getter
@AllArgsConstructor
public enum BdkAppErrorCode {

  AUTH_FAILURE(HttpStatus.UNAUTHORIZED, "Failed to authenticate the extension app with appId={}"),
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Failed to validate the app token"),
  INVALID_JWT(HttpStatus.UNAUTHORIZED, "Failed to validate the jwt"),
  MISSING_FIELDS(HttpStatus.BAD_REQUEST, "Some fields in the request body are missing");

  private final HttpStatus status;
  private final String message;
}
