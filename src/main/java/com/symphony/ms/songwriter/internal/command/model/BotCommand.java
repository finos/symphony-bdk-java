package com.symphony.ms.songwriter.internal.command.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import com.symphony.ms.songwriter.internal.command.CommandDispatcher;
import com.symphony.ms.songwriter.internal.event.model.MessageEvent;

public class BotCommand {
  private static final Logger LOGGER = LoggerFactory.getLogger(BotCommand.class);

  private static final String STREAM_ID = "streamId";
  private static final String USER_ID = "userId";
  private static final String ORIGINAL_TX_ID = "originalTransactionId";

  private CommandDispatcher dispatcher;
  private String channel;
  private MessageEvent event;
  private String originalTransactionId;

  public BotCommand(String channel, MessageEvent event,
      CommandDispatcher dispatcher) {
    this.channel = channel;
    this.event = event;
    this.dispatcher = dispatcher;
    originalTransactionId = MDC.get("transactionId");
  }

  public String getMessage() {
    return event.getMessage();
  }

  public String getUserId() {
    return event.getUserId();
  }

  public String getUserDisplayName() {
    return event.getUserDisplayName();
  }

  public String getStreamId() {
    return event.getStreamId();
  }

  public String getInitialTransactionId() {
    return originalTransactionId;
  }

  public void retry() {
    setMDCContext();
    LOGGER.info("Retrying command: {}", channel);
    dispatcher.push(channel, this);
  }

  private void setMDCContext() {
    MDC.put(STREAM_ID, this.getStreamId());
    MDC.put(USER_ID, this.getUserId());
    MDC.put(ORIGINAL_TX_ID, this.getInitialTransactionId());
  }

}
