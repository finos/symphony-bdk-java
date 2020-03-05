package com.symphony.bot.sdk.internal.command.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.symphony.bot.sdk.internal.command.CommandDispatcher;
import com.symphony.bot.sdk.internal.event.model.MessageEvent;
import com.symphony.bot.sdk.internal.event.model.StreamDetails;
import com.symphony.bot.sdk.internal.event.model.UserDetails;

import lombok.Getter;
import lombok.Setter;

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

  @Getter @Setter
  private MessageEvent messageEvent;
  @Setter
  private CommandDispatcher dispatcher;
  private String channel;
  private String originalTransactionId;

  public BotCommand(String channel, MessageEvent event, CommandDispatcher dispatcher) {
    this.channel = channel;
    this.dispatcher = dispatcher;
    this.messageEvent = event;
    originalTransactionId = MDC.get("transactionId");
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
    if (messageEvent.getUserId() != null) {
      MDC.put(USER_ID, messageEvent.getUserId().toString());
    }
    MDC.put(ORIGINAL_TX_ID, originalTransactionId);
  }

}
