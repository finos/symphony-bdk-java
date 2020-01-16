package com.symphony.ms.bot.sdk.internal.symphony;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.symphony.ms.bot.sdk.internal.symphony.exception.SymphonyClientException;
import clients.SymBotClient;
import model.OutboundMessage;

@Service
public class MessageClientImpl implements MessageClient {
  private static final Logger LOGGER = LoggerFactory.getLogger(MessageClientImpl.class);

  private SymBotClient symBotClient;

  public MessageClientImpl(SymBotClient symBotClient) {
    this.symBotClient = symBotClient;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void sendMessage(String streamId, String message, String jsonData)
      throws SymphonyClientException {
    OutboundMessage outMessage = null;
    if (jsonData == null) {
      outMessage = new OutboundMessage(message);
    } else {
      outMessage = new OutboundMessage(message, jsonData);
    }

    internalSendMessage(streamId, outMessage);
  }

  private void internalSendMessage(String streamId, OutboundMessage message)
      throws SymphonyClientException {
    LOGGER.debug("Sending message to stream: {}", streamId);
    try {
      symBotClient.getMessagesClient().sendMessage(streamId, message);
    } catch (Exception e) {
      LOGGER.error("Error sending message to stream: {}", streamId);
      throw new SymphonyClientException(e);
    }
  }

}
