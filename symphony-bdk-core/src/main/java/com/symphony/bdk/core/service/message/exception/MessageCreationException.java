package com.symphony.bdk.core.service.message.exception;

import org.apiguardian.api.API;

/**
 * Exception thrown when a {@link com.symphony.bdk.core.service.message.model.Message} is failed to create.
 */
@API(status = API.Status.STABLE)
public class MessageCreationException extends RuntimeException {

  public MessageCreationException(String message) {
    super(message);
  }

  public MessageCreationException(String message, Exception e) {
    super(message, e);
  }
}
