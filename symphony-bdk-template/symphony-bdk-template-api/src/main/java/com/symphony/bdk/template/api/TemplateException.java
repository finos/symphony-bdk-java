package com.symphony.bdk.template.api;

import org.apiguardian.api.API;


/**
 * Exception thrown when instantiating a {@link Template} through {@link TemplateEngine}
 * or when calling {@link Template#process(Object)}.
 * This can be triggered when template cannot be loaded,
 * if template is malformed or if some parameters are missing when calling {@link Template#process(Object)}
 */
@API(status = API.Status.STABLE)
public class TemplateException extends Exception {
  public TemplateException(String message, Throwable cause) {
    super(message, cause);
  }
}
