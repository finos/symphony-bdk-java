package com.symphony.bot.sdk.internal.lib.templating;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Handlebars-based implementation of the {@link TemplateService}
 *
 * @author Marcus Secato
 */
public class TemplateServiceImpl implements TemplateService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TemplateServiceImpl.class);

  private Handlebars handlebars;

  public TemplateServiceImpl(Handlebars handlebars) {
    this.handlebars = handlebars;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String processTemplateFile(String templateFile, Object data) {
    Template template = null;
    try {
      template = handlebars.compile(templateFile);
    } catch (IOException e) {
      LOGGER.error("Failed to compile template file: {}\n{}", templateFile, e);
    }
    return applyDataToTemplate(template, data);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String processTemplateString(String templateString, Object data) {
    Template template = null;
    try {
      template = handlebars.compileInline(templateString);
    } catch (IOException e) {
      LOGGER.error("Failed to compile template string: {}\n{}", templateString, e);
    }
    return applyDataToTemplate(template, data);

  }

  private String applyDataToTemplate(Template template, Object data) {
    try {
      return template.apply(data);
    } catch (IOException e) {
      LOGGER.error("Failed to process template: {}", template, e);
      throw new TemplateProcessingException();
    }
  }

}
