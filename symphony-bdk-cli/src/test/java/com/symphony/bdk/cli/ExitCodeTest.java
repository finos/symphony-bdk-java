package com.symphony.bdk.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.symphony.bdk.cli.internal.BdkCliExecutionExceptionHandler;
import com.symphony.bdk.cli.internal.NotFoundException;
import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.exception.BdkConfigException;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;

import org.junit.jupiter.api.Test;

/** Verifies the documented exit-code contract and the JSON error envelope on stderr. */
class ExitCodeTest extends CliTestBase {

  @Test
  void exitCodeMappingUnit() {
    assertThat(BdkCliExecutionExceptionHandler.exitCodeFor(new AuthUnauthorizedException("x"))).isEqualTo(2);
    assertThat(BdkCliExecutionExceptionHandler.exitCodeFor(new AuthInitializationException("x"))).isEqualTo(2);
    assertThat(BdkCliExecutionExceptionHandler.exitCodeFor(new NotFoundException("x"))).isEqualTo(3);
    assertThat(BdkCliExecutionExceptionHandler.exitCodeFor(new ApiException(404, "x"))).isEqualTo(3);
    assertThat(BdkCliExecutionExceptionHandler.exitCodeFor(new ApiRuntimeException(new ApiException(404, "x")))).isEqualTo(3);
    assertThat(BdkCliExecutionExceptionHandler.exitCodeFor(new ApiRuntimeException(new ApiException(500, "x")))).isEqualTo(1);
    assertThat(BdkCliExecutionExceptionHandler.exitCodeFor(new RuntimeException("x"))).isEqualTo(1);
    // cause-chain unwrapping
    assertThat(BdkCliExecutionExceptionHandler.exitCodeFor(new RuntimeException(new AuthUnauthorizedException("x")))).isEqualTo(2);
  }

  @Test
  void authFailureExitsTwo() {
    final BdkCli app = new BdkCli() {
      @Override
      public SymphonyBdk bdk() throws AuthUnauthorizedException {
        throw new AuthUnauthorizedException("invalid bot credentials");
      }
    };
    final int code = run(app, "whoami");
    assertThat(code).isEqualTo(2);
    assertThat(stderr()).contains("\"error\"").contains("\"type\"").contains("AuthUnauthorizedException");
    assertThat(stdout()).isEmpty();
  }

  @Test
  void configErrorExitsNonZero() {
    final BdkCli app = new BdkCli() {
      @Override
      public SymphonyBdk bdk() throws BdkConfigException {
        throw new BdkConfigException("config not found");
      }
    };
    final int code = run(app, "whoami");
    assertThat(code).isNotZero();
    assertThat(stderr()).contains("config not found");
  }

  @Test
  void notFoundFromNullResultExitsThree() {
    final SymphonyBdk bdk = mock(SymphonyBdk.class);
    final MessageService messages = mock(MessageService.class);
    when(bdk.messages()).thenReturn(messages);
    when(messages.getMessage("missing")).thenReturn(null);

    final int code = execute(bdk, "message", "get", "missing");
    assertThat(code).isEqualTo(3);
    assertThat(stderr()).contains("\"error\"").contains("missing");
  }

  @Test
  void notFoundFromApi404ExitsThree() {
    final SymphonyBdk bdk = mock(SymphonyBdk.class);
    final MessageService messages = mock(MessageService.class);
    when(bdk.messages()).thenReturn(messages);
    when(messages.getMessage("gone")).thenThrow(new ApiRuntimeException(new ApiException(404, "not found")));

    final int code = execute(bdk, "message", "get", "gone");
    assertThat(code).isEqualTo(3);
  }

  @Test
  void genericErrorExitsOne() {
    final SymphonyBdk bdk = mock(SymphonyBdk.class);
    final MessageService messages = mock(MessageService.class);
    when(bdk.messages()).thenReturn(messages);
    when(messages.getMessage("boom")).thenThrow(new RuntimeException("kaboom"));

    final int code = execute(bdk, "message", "get", "boom");
    assertThat(code).isEqualTo(1);
    assertThat(stderr()).contains("kaboom");
  }

  @Test
  void missingRequiredOptionExitsUsage64() {
    final int code = execute(mock(SymphonyBdk.class), "message", "send", "STREAM_ID");
    assertThat(code).isEqualTo(64);
  }

  @Test
  void unknownSubcommandExitsUsage64() {
    final int code = execute(mock(SymphonyBdk.class), "definitely-not-a-command");
    assertThat(code).isEqualTo(64);
  }

  @Test
  void ambiguousUserIdentifierExitsUsage64() {
    final SymphonyBdk bdk = mock(SymphonyBdk.class);
    final int code = execute(bdk, "user", "get", "not-an-id-or-email");
    assertThat(code).isEqualTo(64);
  }
}
