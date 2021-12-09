package com.symphony.bdk.core.activity.exception;

import org.apiguardian.api.API;

@API(status = API.Status.STABLE)
public class SlashCommandSyntaxException extends RuntimeException {
  public SlashCommandSyntaxException(String message, Throwable cause) {
    super(message, cause);
  }
}
