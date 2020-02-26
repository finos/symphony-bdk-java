package com.symphony.ms.bot.sdk.internal.symphony;

/**
 * Holds bot and extension app settings defined in json file (either lb-config
 * or bot-config).
 *
 * @author msecato
 *
 */
public interface ConfigClient {

  /**
   * @return the URL path used to enforce authentication
   */
  String getExtAppAuthPath();

  /**
   * @return the extension app ID
   */
  String getExtAppId();

  /**
   * @return the pod base url
   */
  String getPodBaseUrl();
}
