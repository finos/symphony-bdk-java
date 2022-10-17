package com.symphony.bdk.core.config.exception;

import org.apiguardian.api.API;

/**
 * Thrown when the bot configuration is not specified. The BDK will only be runnable in OBO mode.
 */
@API(status = API.Status.STABLE)
public class BotNotConfiguredException extends RuntimeException {

  private static final String NO_BOT_CONFIG_MESSAGE =
      "Bot (service account) credentials have not been configured. You can however use services in OBO mode if app authentication is configured.";

  public BotNotConfiguredException() {
    super(NO_BOT_CONFIG_MESSAGE);
  }
}
