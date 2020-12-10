package com.symphony.bdk.core.config.exception;

import org.apiguardian.api.API;

/**
 * Thrown when the bot configuration is not specified. The BDK will only be runnable in OBO mode.
 */
@API(status = API.Status.STABLE)
public class BotNotConfiguredException extends RuntimeException {

  private static final String NO_BOT_CONFIG_MESSAGE =
      "Bot info is not configured. The bot can be now only runnable only in OBO if the app authentication info is configured";

  public BotNotConfiguredException() {
    super(NO_BOT_CONFIG_MESSAGE);
  }
}
