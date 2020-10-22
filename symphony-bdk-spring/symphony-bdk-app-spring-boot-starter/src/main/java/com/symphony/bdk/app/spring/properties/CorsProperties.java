package com.symphony.bdk.app.spring.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration Properties for enabling the CORS support to accept requests coming from the extension app.
 */
@Getter
@Setter
public class CorsProperties {

  /**
   * CORS handling for the specified path pattern.
   */
  private String allowedOrigin = "/**";

  /**
   * The list of allowed origins pattern.
   */
  private String urlMapping = "*";
}
