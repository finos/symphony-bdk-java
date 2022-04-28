package com.symphony.bdk.spring.service;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.service.datafeed.DatafeedLoop;
import com.symphony.bdk.http.api.ApiException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Collections;

/**
 * Just for the sake of coverage.
 * DatafeedAsyncLauncherService actually catches all the exceptions thrown by DatafeedLoop::start
 */
class DatafeedAsyncLauncherServiceTest {

  private DatafeedAsyncLauncherService datafeedAsyncLauncherService;

  private DatafeedLoop datafeedLoop;

  @BeforeEach
  void setUp() {
    this.datafeedLoop = mock(DatafeedLoop.class);
    this.datafeedAsyncLauncherService = new DatafeedAsyncLauncherService(this.datafeedLoop, Collections.emptyList());
  }

  @Test
  void testSuccess() throws AuthUnauthorizedException, ApiException {
    doNothing().when(datafeedLoop).start();

    datafeedAsyncLauncherService.start();
  }

  @Test
  void testStartLoopThrowsAuthUnauthorizedException() throws AuthUnauthorizedException, ApiException {
    doThrow(new AuthUnauthorizedException("")).when(datafeedLoop).start();

    datafeedAsyncLauncherService.start();
  }

  @Test
  void testStartLoopThrowsApiExceptionException() throws AuthUnauthorizedException, ApiException {
    doThrow(new ApiException(502, "")).when(datafeedLoop).start();

    datafeedAsyncLauncherService.start();
  }

  @Test
  void testStartLoopThrowsConnectException() throws AuthUnauthorizedException, ApiException {
    doThrow(new RuntimeException(new Exception(new ConnectException()))).when(datafeedLoop).start();

    datafeedAsyncLauncherService.start();
  }

  @Test
  void testStartLoopThrowsSocketTimeoutException() throws AuthUnauthorizedException, ApiException {
    doThrow(new RuntimeException(new Exception(new SocketTimeoutException()))).when(datafeedLoop).start();

    datafeedAsyncLauncherService.start();
  }

  @Test
  void testStartLoopUnknownException() throws AuthUnauthorizedException, ApiException {
    doThrow(new RuntimeException(new IllegalStateException())).when(datafeedLoop).start();

    datafeedAsyncLauncherService.start();
  }

  @Test
  void testStartLoopUnknownExceptionTwoCauses() throws AuthUnauthorizedException, ApiException {
    doThrow(new RuntimeException(new Exception(new IllegalStateException()))).when(datafeedLoop).start();

    datafeedAsyncLauncherService.start();
  }
}
