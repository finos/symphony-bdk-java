package com.symphony.bdk.http.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apiguardian.api.API;

import java.io.InputStream;

/**
 *
 */
@Getter
@AllArgsConstructor
@API(status = API.Status.INTERNAL)
public class ApiClientBodyPart {

  private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

  private final InputStream content;
  private final String filename;
  private final String contentType;

  public ApiClientBodyPart(InputStream content, String filename) {
    this(content, filename, DEFAULT_CONTENT_TYPE);
  }
}
