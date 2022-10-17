package com.symphony.bdk.core.activity.exception;

import org.apiguardian.api.API;

/**
 * This exception is triggered when a {@link com.symphony.bdk.core.activity.command.SlashCommand}
 * is instantiated with an invalid command pattern.
 */
@API(status = API.Status.STABLE)
public class SlashCommandSyntaxException extends RuntimeException {
  public SlashCommandSyntaxException(String message, Throwable cause) {
    super(message, cause);
  }

  public SlashCommandSyntaxException(String message) {
    super(message);
  }
}
