package com.symphony.ms.bot.sdk.internal.symphony;

import com.symphony.ms.bot.sdk.internal.symphony.exception.SymphonyClientException;

/**
 * Message client
 *
 * @author msecato
 *
 */
public interface MessageClient {

  /**
   * Sends message to a Symphony stream
   *
   * @param streamId
   * @param message
   * @param jsonData
   * @throws SymphonyClientException on error connecting to Symphony
   */
  void sendMessage(String streamId, String message, String jsonData)
      throws SymphonyClientException;

}
