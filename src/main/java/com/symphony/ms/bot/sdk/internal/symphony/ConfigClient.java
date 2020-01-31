package com.symphony.ms.bot.sdk.internal.symphony;

/**
 * Holds bot and extension app settings defined in json file (either lb-config
 * or bot-config).
 *
 * @author msecato
 *
 */
public interface ConfigClient {

  String getExtAppAuthPath();

}
