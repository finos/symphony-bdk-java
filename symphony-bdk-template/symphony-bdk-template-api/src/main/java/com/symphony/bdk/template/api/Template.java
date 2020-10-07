package com.symphony.bdk.template.api;

import org.apiguardian.api.API;

/**
 * Interface to represent a template.
 * A template takes parameters in input and outputs a string.
 */
@API(status = API.Status.STABLE)
public interface Template {

  /**
   * Produces the string from this template using the given parameters passed in input.
   * @param parameters the object which contains the parameters to be used by the template.
   *                   The actual type depends on the concrete implementation.
   *                   Checking the actual type passed in parameter will be the responsibility of each concrete implementation.
   * @return the generated string where each parameter is replaced by its value provided in the map
   * @throws TemplateException in case of issues during the string generation, e.g. missing parameter
   */
  String process(Object parameters);
}
