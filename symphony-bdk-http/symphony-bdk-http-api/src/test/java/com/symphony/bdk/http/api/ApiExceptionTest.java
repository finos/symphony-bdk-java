package com.symphony.bdk.http.api;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ApiExceptionTest {

  @Test
  void isServerError() {
    assertTrue(new ApiException(500, "Internal Server Error").isServerError());
    assertTrue(new ApiException(502, "Internal Server Error").isServerError());
    assertTrue(new ApiException(503, "Internal Server Error").isServerError());
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
