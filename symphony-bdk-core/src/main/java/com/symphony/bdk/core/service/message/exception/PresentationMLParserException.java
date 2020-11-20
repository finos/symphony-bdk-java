package com.symphony.bdk.core.service.message.exception;

import com.symphony.bdk.core.service.message.util.PresentationMLParser;

import lombok.Getter;
import org.apiguardian.api.API;

/**
 * Exception thrown when the {@link PresentationMLParser} failed to parse a PresentationML string.
 */
@API(status = API.Status.STABLE)
public class PresentationMLParserException extends Exception {

  @Getter private final String presentationML;

  public PresentationMLParserException(String presentationML, String message, Exception e) {
    super(message, e);
    this.presentationML = presentationML;
  }
}
