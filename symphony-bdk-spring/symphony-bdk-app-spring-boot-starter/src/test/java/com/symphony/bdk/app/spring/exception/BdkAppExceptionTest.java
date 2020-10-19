package com.symphony.bdk.app.spring.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BdkAppExceptionTest {

  @Test
  void shouldBuildAppErrorWithParams() {
    final BdkAppException ex = new BdkAppException(BdkAppErrorCode.AUTH_FAILURE, "appId-1234");
    final BdkAppError error = BdkAppError.fromException(ex);
    assertEquals(1, error.getMessage().size());
    assertTrue(error.getMessage().get(0).contains("appId-1234"));
  }

  @Test
  void shouldBuildAppErrorWithNoParam() {
    final BdkAppException ex = new BdkAppException(BdkAppErrorCode.INVALID_JWT);
    final BdkAppError error = BdkAppError.fromException(ex);
    assertEquals(1, error.getMessage().size());
    assertEquals("Failed to validate the jwt", error.getMessage().get(0));
  }
}
