package com.symphony.ms.bot.sdk.internal.command.model;

import com.symphony.ms.bot.sdk.internal.command.CommandDispatcher;
import com.symphony.ms.bot.sdk.internal.event.model.MessageEvent;
import com.symphony.ms.bot.sdk.internal.event.model.StreamDetails;
import com.symphony.ms.bot.sdk.internal.event.model.UserDetails;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Holds the bot command details
 *
 * @author Marcus Secato
 */
@Data
public class BotCommand {
  private static final Logger LOGGER = LoggerFactory.getLogger(BotCommand.class);

  private static final String STREAM_ID = "streamId";
  private static final String USER_ID = "userId";
  private static final String ORIGINAL_TX_ID = "originalTransactionId";

  private CommandDispatcher dispatcher;
  private String channel;
  private MessageEvent messageEvent;
  private String originalTransactionId;

  public BotCommand(String channel, MessageEvent event, CommandDispatcher dispatcher) {
    this.channel = channel;
    this.dispatcher = dispatcher;
    this.messageEvent = event;
    originalTransactionId = MDC.get("transactionId");
  }

  public BotCommand(String channel, CommandDispatcher dispatcher) {
    this.channel = channel;
    this.dispatcher = dispatcher;
  }

  public UserDetails getUser() {
    return messageEvent.getUser();
  }

  public StreamDetails getStream() {
    return messageEvent.getStream();
  }

  public void retry() {
    setMDCContext();
    LOGGER.info("Retrying command: {}", channel);
    dispatcher.push(channel, this);
  }

  private void setMDCContext() {
    MDC.put(STREAM_ID, messageEvent.getStreamId());
    MDC.put(USER_ID, messageEvent.getUserId());
    MDC.put(ORIGINAL_TX_ID, originalTransactionId);
  }

}
