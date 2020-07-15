package com.symphony.bdk.bot.sdk.symphony;

import java.util.List;

import com.symphony.bdk.bot.sdk.event.model.MessageAttachmentFile;
import com.symphony.bdk.bot.sdk.event.model.MessageEvent;
import com.symphony.bdk.bot.sdk.symphony.exception.SymphonyClientException;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyMessage;

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
   * @param streamId the stream ID
   * @param message the message to be sent
   * @throws SymphonyClientException on error connecting to Symphony
   */
  void sendMessage(String streamId, SymphonyMessage message)
      throws SymphonyClientException;

  /**
   * Sends message to a Symphony stream
   *
   * @param streamId the stream ID
   * @param message the message to be sent
   * @param jsonData the message data
   * @throws SymphonyClientException on error connecting to Symphony
   */
  void sendMessage(String streamId, String message, String jsonData)
      throws SymphonyClientException;

  /**
   * Sends message with attachments to a Symphony stream
   *
   * @param streamId the stream ID
   * @param message the message to be sent
   * @param jsonData the message data
   * @param attachments the attachments list
   * @throws SymphonyClientException on error connecting to Symphony
   */
  void sendMessage(String streamId, String message, String jsonData,
      List<MessageAttachmentFile> attachments) throws SymphonyClientException;

  /**
   * Download specific attachments from a message from a stream to memory
   *
   * @param messageEvent the message event
   * @return the attachments the attachments list
   * @throws SymphonyClientException on error downloading from Symphony
   */
  List<MessageAttachmentFile> downloadMessageAttachments(MessageEvent messageEvent)
      throws SymphonyClientException;
}
