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

  private final InputStream content;
  private final String filename;
}
