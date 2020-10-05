package com.symphony.bdk.template.handlebars;

import com.github.jknack.handlebars.Handlebars;
import com.symphony.bdk.template.api.Template;
import com.symphony.bdk.template.api.TemplateException;
import org.apiguardian.api.API;

import java.io.IOException;

/**
 * {@link Handlebars} implementation of the {@link Template} interface.
 */
@API(status = API.Status.INTERNAL)
public class HandlebarsTemplate implements Template {

  private final com.github.jknack.handlebars.Template template;

  public HandlebarsTemplate(com.github.jknack.handlebars.Template template) {
    this.template = template;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String process(Object parameters) throws TemplateException {
    try {
      return this.template.apply(parameters);
    } catch (IOException e) {
      throw new TemplateException("Could not generate string from template", e);
    }
  }
}
