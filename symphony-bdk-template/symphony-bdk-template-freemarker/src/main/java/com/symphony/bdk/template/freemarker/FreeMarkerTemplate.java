package com.symphony.bdk.template.freemarker;


import com.symphony.bdk.template.api.Template;
import com.symphony.bdk.template.api.TemplateException;

import org.apiguardian.api.API;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

/**
 * FreeMarker specific implementation of {@link Template}
 */
@API(status = API.Status.INTERNAL)
public class FreeMarkerTemplate implements Template {

  private final freemarker.template.Template template;

  public FreeMarkerTemplate(freemarker.template.Template template) {
    this.template = template;
  }

  /**
   * Produces the string from this template using the given parameters passed in input.
   * @param parameters the object which contains the parameters to be used by the template.
   *                   Checlk FreeMarker documentation to know more about accepted objects:
   *                   @see <a href="https://freemarker.apache.org/docs/pgui_quickstart_createdatamodel.html">Create the data model</a>
   * @return the generated string where each parameter is replaced by its value provided in the map
   * @throws TemplateException in case of issues during the string generation, e.g. missing parameter
   */
  @Override
  public String process(Object parameters) throws TemplateException {
    try {
      Writer out = new StringWriter();
      template.process(parameters, out);
      return out.toString();
    } catch (freemarker.template.TemplateException | IOException e) {
      throw new TemplateException("Could not generate string from template", e);
    }
  }
}
