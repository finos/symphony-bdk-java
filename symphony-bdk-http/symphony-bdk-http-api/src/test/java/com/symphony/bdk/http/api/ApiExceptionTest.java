package com.symphony.bdk.http.api;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ApiExceptionTest {

  @Test
  void isServerError() {
    assertFalse(new ApiException(499, "An error").isServerError());
    assertTrue(new ApiException(500, "Internal Server Error").isServerError());
    assertTrue(new ApiException(502, "Bad Gateway").isServerError());
    assertTrue(new ApiException(503, "Service Unavailable").isServerError());
  }

  @Test
  void isUnauthorized() {
    assertTrue(new ApiException(401, "Unauthorized").isUnauthorized());
  }

  @Test
  void isClientError() {
    assertTrue(new ApiException(400, "Bad Request").isClientError());
  }

  @Test
  void isTooManyRequestsError() {
    assertTrue(new ApiException(429, "Too Many Requests").isTooManyRequestsError());
  }
}
