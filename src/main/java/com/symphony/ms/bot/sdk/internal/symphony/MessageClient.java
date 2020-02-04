package com.symphony.ms.bot.sdk.internal.symphony;

import java.util.List;
import com.symphony.ms.bot.sdk.internal.event.model.MessageAttachmentFile;
import com.symphony.ms.bot.sdk.internal.event.model.MessageEvent;
import com.symphony.ms.bot.sdk.internal.symphony.exception.SymphonyClientException;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyMessage;

/**
 * Message client
 * Manages sending message to Symphony.
 *
 * @author msecato
 */
public interface MessageClient {

  /**
   * Sends message to the specified stream applying template processing when applicable.
   *
   * @param streamId
   * @param message
   * @throws SymphonyClientException on error connecting to Symphony
   */
  void sendMessage(String streamId, SymphonyMessage message)
      throws SymphonyClientException;

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

  /**
   * Sends message with attachments to a Symphony stream
   *
   * @param streamId
   * @param message
   * @param jsonData
   * @param attachments
   * @throws SymphonyClientException on error connecting to Symphony
   */
  void sendMessage(String streamId, String message, String jsonData,
      List<MessageAttachmentFile> attachments) throws SymphonyClientException;

  /**
   * Download specific attachments from a message from a stream to memory
   *
   * @param messageEvent
   * @return the attachments
   * @throws SymphonyClientException on error downloading from Symphony
   */
  List<MessageAttachmentFile> downloadMessageAttachments(MessageEvent messageEvent)
      throws SymphonyClientException;
}
