package com.symphony.ms.bot.sdk.internal.command.model;

import com.symphony.ms.bot.sdk.internal.command.CommandDispatcher;
import com.symphony.ms.bot.sdk.internal.event.model.MessageEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Holds the bot command details
 *
 * @author Marcus Secato
 */
public class BotCommand {
  private static final Logger LOGGER = LoggerFactory.getLogger(BotCommand.class);

  private static final String STREAM_ID = "streamId";
  private static final String USER_ID = "userId";
  private static final String ORIGINAL_TX_ID = "originalTransactionId";

  private CommandDispatcher dispatcher;
  private String channel;
  private String message;
  private String userId;
  private String userDisplayName;
  private String streamId;
  private String originalTransactionId;

  public BotCommand(String channel, MessageEvent event, CommandDispatcher dispatcher) {
    this.channel = channel;
    this.dispatcher = dispatcher;
    this.message = event.getMessage();
    this.userId = event.getUserId();
    this.userDisplayName = event.getUser().getDisplayName();
    this.streamId = event.getStreamId();
    originalTransactionId = MDC.get("transactionId");
  }

  public BotCommand(String channel, CommandDispatcher dispatcher) {
    this.channel = channel;
    this.dispatcher = dispatcher;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUserDisplayName() {
    return userDisplayName;
  }

  public void setUserDisplayName(String userDisplayName) {
    this.userDisplayName = userDisplayName;
  }

  public String getStreamId() {
    return streamId;
  }

  public void setStreamId(String streamId) {
    this.streamId = streamId;
  }

  public void retry() {
    setMDCContext();
    LOGGER.info("Retrying command: {}", channel);
    dispatcher.push(channel, this);
  }

  private void setMDCContext() {
    MDC.put(STREAM_ID, this.getStreamId());
    MDC.put(USER_ID, this.getUserId());
    MDC.put(ORIGINAL_TX_ID, originalTransactionId);
  }

}
