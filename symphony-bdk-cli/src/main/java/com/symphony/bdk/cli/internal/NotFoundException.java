package com.symphony.bdk.cli.internal;

/**
 * Raised when a command targets a resource (message, stream, user, …) that does not exist.
 * Mapped to exit code {@code 3} by {@link BdkCliExecutionExceptionHandler}.
 */
public class NotFoundException extends RuntimeException {

  public NotFoundException(String message) {
    super(message);
  }

  /** Convenience factory: {@code no <kind> found for '<identifier>'}. */
  public static NotFoundException of(String kind, Object identifier) {
    return new NotFoundException("no " + kind + " found for '" + identifier + "'");
  }
}
