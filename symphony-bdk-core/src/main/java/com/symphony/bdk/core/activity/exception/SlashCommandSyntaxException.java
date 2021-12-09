package com.symphony.bdk.core.activity.exception;

public class SlashCommandSyntaxException extends RuntimeException {
  public SlashCommandSyntaxException(String message, Throwable cause) {
    super(message, cause);
  }
}
