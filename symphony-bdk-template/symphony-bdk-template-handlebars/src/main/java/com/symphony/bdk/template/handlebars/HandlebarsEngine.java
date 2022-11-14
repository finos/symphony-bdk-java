package com.symphony.bdk.template.handlebars;

import com.symphony.bdk.template.api.Template;
import com.symphony.bdk.template.api.TemplateEngine;
import com.symphony.bdk.template.api.TemplateException;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import org.apache.commons.io.FilenameUtils;
import org.apiguardian.api.API;

import java.io.IOException;

/**
 * {@link Handlebars} implementation of the {@link TemplateEngine} interface.
 *
 * <p>
 * This class is thread-safe.
 * </p>
 */
@API(status = API.Status.INTERNAL)
public class HandlebarsEngine implements TemplateEngine {

  /**
   * Handlebars for classpath loading. Ok for thread-safety.
   */
  private static final Handlebars HANDLEBARS = createHandlebars(new ClassPathTemplateLoader());

  /**
   * {@inheritDoc}
   */
  @Override
  public Template newTemplateFromFile(String templatePath) {
    final String basedir = FilenameUtils.getFullPathNoEndSeparator(templatePath);
    final String file = FilenameUtils.getName(templatePath);
    // for thread-safety, we need to create a specific Handlebars object
    final Handlebars handlebars = createHandlebars(new FileTemplateLoader(basedir));
    try {
      return new HandlebarsTemplate(handlebars.compile(file));
    } catch (IOException e) {
      throw new TemplateException("Unable to compile Handlebars template from file location: " + templatePath, e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Template newTemplateFromClasspath(String templatePath) {
    try {
      return new HandlebarsTemplate(HANDLEBARS.compile(templatePath));
    } catch (IOException e) {
      throw new TemplateException("Unable to compile Handlebars template from classpath location: " + templatePath, e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Template newTemplateFromString(String template) {
    try {
      return new HandlebarsTemplate(HANDLEBARS.compileInline(template));
    } catch (IOException e) {
      throw new TemplateException("Unable to compile Handlebars template from inline string: " + template, e);
    }
  }



  /**
   * Creates a new {@link Handlebars} object with suffix set to "" to make this {@link TemplateEngine} implementation
   * consistent with other ones (e.g. developers have to specify the template resource extension).
   */
  private static Handlebars createHandlebars(final TemplateLoader templateLoader) {
    final Handlebars handlebars = new Handlebars(templateLoader);
    handlebars.getLoader().setSuffix("");
    return handlebars;
  }
}
