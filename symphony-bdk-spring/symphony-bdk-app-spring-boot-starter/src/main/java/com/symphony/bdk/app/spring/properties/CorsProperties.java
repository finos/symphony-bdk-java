package com.symphony.bdk.app.spring.properties;

import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;

import java.util.Collections;
import java.util.List;

/**
 * Configuration Properties for enabling the CORS support to accept requests coming from the extension app.
 */
@Getter
@Setter
public class CorsProperties {

  /**
   * CORS handling for the specified path patterns.
   */
  private List<String> allowedOrigins = Collections.singletonList("/**");

  /**
   * @deprecated use property "allow-credentials" instead
   */
  @Deprecated
  @API(status = API.Status.DEPRECATED, since = "2.0")
  private Boolean allowedCredentials = null;

  /**
   * Access-Control-Allow-Credentials response header for CORS request
   */
  private Boolean allowCredentials = false;

  /**
   * List of headers that a request can list as allowed
   */
  private List<String> allowedHeaders = Collections.emptyList();

  /**
   * List of response headers that a response can have and can be exposed
   */
  private List<String> exposedHeaders = Collections.emptyList();

  /**
   * @deprecated use property "allow-credentials" instead
   */
  @Deprecated
  @API(status = API.Status.DEPRECATED, since = "2.0")
  private List<String> allowedMethod = null;

  /**
   * List of HTTP methods to allow
   */
  private List<String> allowedMethods = Collections.emptyList();
}
