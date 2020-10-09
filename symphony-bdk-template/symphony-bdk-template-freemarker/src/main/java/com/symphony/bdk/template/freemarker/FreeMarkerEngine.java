package com.symphony.bdk.template.freemarker;

import com.symphony.bdk.template.api.Template;
import com.symphony.bdk.template.api.TemplateEngine;
import com.symphony.bdk.template.api.TemplateException;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.io.FilenameUtils;
import org.apiguardian.api.API;

import java.io.File;
import java.io.IOException;

/**
 * FreeMarker specific implementation of {@link TemplateEngine}. Instantiates {@link FreeMarkerTemplate} objects.
 *
 * <p>
 *   This class is thread-safe.
 * </p>
 */
@API(status = API.Status.INTERNAL)
public class FreeMarkerEngine implements TemplateEngine {

  /**
   * {@inheritDoc}
   */
  @Override
  public Template newTemplateFromFile(String templatePath) {
    try {
      final String directory = FilenameUtils.getFullPathNoEndSeparator(templatePath);
      final String file = FilenameUtils.getName(templatePath);
      final Configuration configuration = createConfiguration(); // for thread-safety, we need to re-create configuration
      configuration.setDirectoryForTemplateLoading(new File(directory));
      return new FreeMarkerTemplate(configuration.getTemplate(file));
    } catch (IOException e) {
      throw new TemplateException("Unable to open template file", e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Template newTemplateFromClasspath(String templatePath) {
    try {
      final Configuration configuration = createConfiguration(); // for thread-safety, we need to re-create configuration
      configuration.setClassForTemplateLoading(this.getClass(), "/");
      return new FreeMarkerTemplate(configuration.getTemplate(templatePath));
    } catch (IOException e) {
      throw new TemplateException("Unable to load template from classpath", e);
    }
  }

  private static Configuration createConfiguration() {
    final Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
    cfg.setDefaultEncoding("UTF-8");
    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    cfg.setLogTemplateExceptions(false);
    cfg.setWrapUncheckedExceptions(true);
    cfg.setFallbackOnNullLoopVariable(false);
    return cfg;
  }
}
