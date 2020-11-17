package com.symphony.bdk.core.service.message.exception;

import org.apiguardian.api.API;

/**
 * Exception thrown when a {@link com.symphony.bdk.core.service.message.model.Message} is invalid.
 */
@API(status = API.Status.STABLE)
public class MessageValidationException extends RuntimeException {

  public MessageValidationException(String message, Exception e) {
    super(message, e);
  }
}
