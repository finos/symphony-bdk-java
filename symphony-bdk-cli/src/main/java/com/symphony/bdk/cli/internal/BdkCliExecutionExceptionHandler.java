package com.symphony.bdk.cli.internal;

import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;

import picocli.CommandLine;
import picocli.CommandLine.IExecutionExceptionHandler;
import picocli.CommandLine.ParseResult;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Serialises any exception thrown while executing a command to a JSON envelope on {@code stderr}
 * and maps it to a stable, documented exit code.
 *
 * <table>
 *   <caption>Exit codes</caption>
 *   <tr><td>0</td><td>success</td></tr>
 *   <tr><td>1</td><td>generic / unexpected error</td></tr>
 *   <tr><td>2</td><td>authentication failure</td></tr>
 *   <tr><td>3</td><td>not found / API 404</td></tr>
 *   <tr><td>64</td><td>usage error (handled by picocli's default parameter exception handler)</td></tr>
 * </table>
 */
public class BdkCliExecutionExceptionHandler implements IExecutionExceptionHandler {

  /** Authentication failed. */
  public static final int EXIT_AUTH = 2;
  /** Targeted resource does not exist. */
  public static final int EXIT_NOT_FOUND = 3;
  /**
   * Usage error (bad arguments). Set to the conventional {@code EX_USAGE} value rather than
   * picocli's {@code ExitCode.USAGE} ({@code 2}), which would collide with {@link #EXIT_AUTH}.
   */
  public static final int EXIT_USAGE = 64;

  @Override
  public int handleExecutionException(Exception ex, CommandLine cmd, ParseResult parseResult) {
    final Map<String, Object> envelope = new LinkedHashMap<>();
    envelope.put("error", ex.getMessage() != null ? ex.getMessage() : ex.toString());
    envelope.put("type", ex.getClass().getSimpleName());

    cmd.getErr().println(Json.pretty(envelope));
    cmd.getErr().flush();

    return exitCodeFor(ex);
  }

  /** Maps an exception (walking its cause chain) to the documented exit code. */
  public static int exitCodeFor(Throwable ex) {
    for (Throwable t = ex; t != null; t = t.getCause()) {
      if (t instanceof AuthInitializationException || t instanceof AuthUnauthorizedException) {
        return EXIT_AUTH;
      }
      if (t instanceof NotFoundException) {
        return EXIT_NOT_FOUND;
      }
      if (t instanceof ApiRuntimeException && ((ApiRuntimeException) t).getCode() == 404) {
        return EXIT_NOT_FOUND;
      }
      if (t instanceof ApiException && ((ApiException) t).getCode() == 404) {
        return EXIT_NOT_FOUND;
      }
      if (t instanceof CommandLine.ParameterException) {
        return EXIT_USAGE;
      }
    }
    return CommandLine.ExitCode.SOFTWARE; // 1
  }
}
