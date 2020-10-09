package com.symphony.bdk.core.service.message.model;

import com.symphony.bdk.core.service.message.exception.MessageCreationException;

import lombok.Getter;
import org.apiguardian.api.API;

import java.io.InputStream;

/**
 * Attachment model to be used in {@link MessageBuilder} to attach a file to a {@link Message}
 */
@Getter
@API(status = API.Status.EXPERIMENTAL)
public class Attachment {

  private final InputStream inputStream;
  private final String filename;

  public Attachment(InputStream inputStream, String filename) {
    this.inputStream = inputStream;
    if (filename.split("\\.").length < 2 ) {
      throw new MessageCreationException("Invalid attachment's file name.");
    }
    this.filename = filename;
  }
}
