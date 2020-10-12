package com.symphony.bdk.core.service.message.model;

import com.symphony.bdk.core.service.message.exception.MessageCreationException;

import lombok.Getter;
import org.apiguardian.api.API;

import java.io.InputStream;

/**
 * Attachment model to be used in {@link MessageBuilder} to attach a file to a {@link Message}
 */
@Getter
@API(status = API.Status.STABLE)
public class Attachment {

  private final InputStream content;
  private final String filename;

  public Attachment(InputStream content, String filename) {
    this.content = content;
    if (filename.split("\\.").length < 2 ) {
      throw new MessageCreationException("Invalid attachment's filename, extension is missing.");
    }
    this.filename = filename;
  }
}
