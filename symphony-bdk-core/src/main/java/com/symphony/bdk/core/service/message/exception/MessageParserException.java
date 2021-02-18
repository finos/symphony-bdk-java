package com.symphony.bdk.core.service.message.exception;

import org.apiguardian.api.API;

/**
 * Exception thrown when the {@link com.symphony.bdk.core.service.message.util.MessageParser} fails to parse data
 * inside the message.
 */
@API(status = API.Status.EXPERIMENTAL)
public class MessageParserException extends Exception {

  public MessageParserException(String message, Exception e) {
    super(message, e);
  }
}
