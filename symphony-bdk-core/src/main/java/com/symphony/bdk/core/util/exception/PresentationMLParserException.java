package com.symphony.bdk.core.util.exception;

import org.apiguardian.api.API;

/**
 * Exception thrown when the {@link com.symphony.bdk.core.util.PresentationMLParser} failed to parse a PresentationML string.
 */
@API(status = API.Status.STABLE)
public class PresentationMLParserException extends Exception {

  public PresentationMLParserException(String message, Exception e) {
    super(message, e);
  }
}
