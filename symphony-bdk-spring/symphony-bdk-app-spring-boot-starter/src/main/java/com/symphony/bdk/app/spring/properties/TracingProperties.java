package com.symphony.bdk.app.spring.properties;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

/**
 * Activation or deactivation of the {@link com.symphony.bdk.app.spring.filter.TracingFilter}.
 */
@Getter
@Setter
public class TracingProperties {

  /**
   * Activate or deactivate the {@link com.symphony.bdk.app.spring.filter.TracingFilter}.
   */
  private Boolean enabled = true;

  /**
   * Add URL patterns, as defined in the Servlet specification, that the tracing filter will be registered against.
   */
  private List<String> urlPatterns = Collections.singletonList("/*");
}
