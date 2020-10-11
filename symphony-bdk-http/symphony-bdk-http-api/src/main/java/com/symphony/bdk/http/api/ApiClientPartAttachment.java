package com.symphony.bdk.http.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.InputStream;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiClientPartAttachment {

  private InputStream content;
  private String filename;
}
