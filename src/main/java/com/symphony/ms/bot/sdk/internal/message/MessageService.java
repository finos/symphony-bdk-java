package com.symphony.ms.bot.sdk.internal.message;

import com.symphony.ms.bot.sdk.internal.message.model.SymphonyMessage;

/**
 * Manages sending message to Symphony
 *
 * @author Marcus Secato
 *
 */
public interface MessageService {

  /**
   * Sends message to the specified stream
   *
   * @param streamId
   * @param message
   */
  void sendMessage(String streamId, SymphonyMessage message);

}
